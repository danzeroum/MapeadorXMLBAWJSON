package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.MethodologyEntity;
import br.com.danzeroum.processveritas.domain.repository.MethodologyRepository;
import br.com.danzeroum.processveritas.domain.repository.UserRepository;
import br.com.danzeroum.processveritas.web.dto.request.CreateMethodologyRequest;
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
public class MethodologyService {

    private static final Logger log = LoggerFactory.getLogger(MethodologyService.class);

    @Autowired
    private MethodologyRepository methodologyRepo;

    @Autowired
    private UserRepository userRepo;

    public Page<MethodologyEntity> list(Pageable pageable) {
        return methodologyRepo.findAllByOrderByCreatedAtDesc(pageable);
    }

    public MethodologyEntity getActive() {
        return methodologyRepo.findByActiveTrue()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active methodology found"));
    }

    public MethodologyEntity getById(UUID id) {
        return methodologyRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Methodology not found: " + id));
    }

    @Transactional
    public MethodologyEntity create(CreateMethodologyRequest request, UUID createdByUserId) {
        methodologyRepo.findByActiveTrue().ifPresent(active -> {
            active.setActive(false);
            methodologyRepo.save(active);
        });

        MethodologyEntity entity = new MethodologyEntity();
        entity.setVersion(request.getVersion());
        entity.setWeights(request.getWeights());
        entity.setActive(true);

        if (createdByUserId != null) {
            userRepo.findById(createdByUserId).ifPresent(entity::setCreatedBy);
        }

        MethodologyEntity saved = methodologyRepo.save(entity);
        log.info("Methodology created: id={} version={}", saved.getId(), saved.getVersion());
        return saved;
    }
}
