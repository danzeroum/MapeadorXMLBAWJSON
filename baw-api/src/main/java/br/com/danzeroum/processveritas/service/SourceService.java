package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.SourceEntity;
import br.com.danzeroum.processveritas.domain.model.UserEntity;
import br.com.danzeroum.processveritas.domain.repository.SourceRepository;
import br.com.danzeroum.processveritas.domain.repository.UserRepository;
import br.com.danzeroum.processveritas.web.dto.request.CreateSourceRequest;
import br.com.danzeroum.processveritas.web.dto.request.UpdateSourceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class SourceService {

    private static final Logger log = LoggerFactory.getLogger(SourceService.class);

    @Autowired
    private SourceRepository sourceRepo;

    @Autowired
    private UserRepository userRepo;

    public Page<SourceEntity> list(Pageable pageable) {
        return sourceRepo.findAll(pageable);
    }

    public SourceEntity getById(UUID id) {
        return sourceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source not found: " + id));
    }

    @Transactional
    public SourceEntity create(CreateSourceRequest request, UUID createdByUserId) {
        SourceEntity entity = new SourceEntity();
        entity.setName(request.getName());
        entity.setType(SourceEntity.SourceType.valueOf(request.getType()));
        entity.setHost(request.getHost());
        entity.setCredentials(request.getCredentials());

        if (createdByUserId != null) {
            userRepo.findById(createdByUserId).ifPresent(entity::setCreatedBy);
        }

        SourceEntity saved = sourceRepo.save(entity);
        log.info("Source created: id={} name={} type={}", saved.getId(), saved.getName(), saved.getType());
        return saved;
    }

    @Transactional
    public SourceEntity update(UUID id, UpdateSourceRequest request) {
        SourceEntity entity = getById(id);

        if (request.getName() != null) entity.setName(request.getName());
        if (request.getHost() != null) entity.setHost(request.getHost());
        if (request.getCredentials() != null) entity.setCredentials(request.getCredentials());
        if (request.getStatus() != null) {
            if (!request.getStatus().equals("ACTIVE") && !request.getStatus().equals("INACTIVE")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + request.getStatus());
            }
            entity.setStatus(request.getStatus());
        }

        SourceEntity saved = sourceRepo.save(entity);
        log.info("Source updated: id={}", id);
        return saved;
    }

    @Transactional
    public void deactivate(UUID id) {
        SourceEntity entity = getById(id);
        entity.setStatus("INACTIVE");
        sourceRepo.save(entity);
        log.info("Source deactivated: id={}", id);
    }
}
