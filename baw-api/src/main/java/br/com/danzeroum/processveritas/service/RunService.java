package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.RunEntity;
import br.com.danzeroum.processveritas.domain.model.RunEntity.RunStatus;
import br.com.danzeroum.processveritas.domain.model.UserEntity;
import br.com.danzeroum.processveritas.domain.repository.RunRepository;
import br.com.danzeroum.processveritas.motor.TwxUnpackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class RunService {

    private static final Logger log = LoggerFactory.getLogger(RunService.class);

    @Autowired
    private RunRepository runRepo;

    @Autowired
    private TwxUnpackService unpackService;

    @Autowired
    private AnalysisOrchestrator orchestrator;

    @Transactional
    public RunEntity createRun(String processName, String processId, MultipartFile twxFile, UserEntity author) throws IOException {
        RunEntity run = new RunEntity();
        run.setProcessName(processName);
        run.setProcessId(processId);
        run.setTwxFilename(twxFile.getOriginalFilename());
        run.setAuthor(author);
        run = runRepo.save(run);

        log.info("Run created: id={} processName={} author={}", run.getId(), processName,
                author != null ? author.getEmail() : "anonymous");

        Path extractionPath = unpackService.unpack(run.getId(), twxFile);
        orchestrator.processRun(run.getId(), extractionPath);
        return run;
    }

    public Page<RunEntity> listRuns(RunStatus status, Pageable pageable) {
        if (status != null) {
            return runRepo.findByStatus(status, pageable);
        }
        return runRepo.findAll(pageable);
    }

    public RunEntity getRunById(UUID id) {
        return runRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Run not found: " + id));
    }

    public void deleteRun(UUID id) {
        if (!runRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Run not found: " + id);
        }
        runRepo.deleteById(id);
        log.info("Run deleted: id={}", id);
    }
}
