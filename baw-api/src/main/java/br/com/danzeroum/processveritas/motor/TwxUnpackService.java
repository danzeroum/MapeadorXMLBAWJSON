package br.com.danzeroum.processveritas.motor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class TwxUnpackService {

    private static final Logger log = LoggerFactory.getLogger(TwxUnpackService.class);

    @Value("${pv.analysis.work-dir:/tmp/pv-runs}")
    private String workDir;

    @Value("${pv.analysis.cleanup-on-finish:true}")
    private boolean cleanupOnFinish;

    public Path unpack(UUID runId, MultipartFile file) throws IOException {
        Path runDir = Path.of(workDir, runId.toString());
        Files.createDirectories(runDir);

        // Save upload
        Path twxPath = runDir.resolve("upload.twx");
        file.transferTo(twxPath.toFile());
        log.info("TWX file saved to {} ({} bytes)", twxPath, Files.size(twxPath));

        // Unzip with ZipSlip protection
        Path extractDir = runDir.resolve("extracted");
        Files.createDirectories(extractDir);

        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(twxPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path target = extractDir.resolve(entry.getName()).normalize();

                // ZipSlip: reject any path that escapes extractDir
                if (!target.startsWith(extractDir)) {
                    throw new SecurityException("ZipSlip attack detected: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(zis, target, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }

        log.info("TWX unpacked to {} for runId={}", extractDir, runId);
        return extractDir;
    }

    public void cleanup(UUID runId) {
        if (!cleanupOnFinish) {
            log.debug("Cleanup disabled; skipping cleanup for runId={}", runId);
            return;
        }
        Path runDir = Path.of(workDir, runId.toString());
        try {
            if (Files.exists(runDir)) {
                Files.walk(runDir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                log.warn("Could not delete {}: {}", path, e.getMessage());
                            }
                        });
                log.info("Cleaned up run directory for runId={}", runId);
            }
        } catch (IOException e) {
            log.error("Failed to clean up run directory for runId={}: {}", runId, e.getMessage());
        }
    }
}
