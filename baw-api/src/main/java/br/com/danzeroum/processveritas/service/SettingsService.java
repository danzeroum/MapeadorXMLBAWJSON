package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.PlatformSettingEntity;
import br.com.danzeroum.processveritas.domain.repository.PlatformSettingRepository;
import br.com.danzeroum.processveritas.domain.repository.UserRepository;
import br.com.danzeroum.processveritas.web.dto.request.UpdateSettingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    @Autowired
    private PlatformSettingRepository settingRepo;

    @Autowired
    private UserRepository userRepo;

    public List<PlatformSettingEntity> listAll() {
        return settingRepo.findAll();
    }

    public PlatformSettingEntity getByKey(String key) {
        return settingRepo.findById(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found: " + key));
    }

    @Transactional
    public PlatformSettingEntity upsert(String key, UpdateSettingRequest request, UUID updatedByUserId) {
        PlatformSettingEntity entity = settingRepo.findById(key).orElse(new PlatformSettingEntity());
        entity.setKey(key);
        entity.setValue(request.getValue());
        entity.setUpdatedAt(OffsetDateTime.now());

        if (updatedByUserId != null) {
            userRepo.findById(updatedByUserId).ifPresent(entity::setUpdatedBy);
        }

        PlatformSettingEntity saved = settingRepo.save(entity);
        log.info("Setting upserted: key={}", key);
        return saved;
    }
}
