package br.com.danzeroum.processveritas.domain.repository;

import br.com.danzeroum.processveritas.domain.model.RunEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RunRepository extends JpaRepository<RunEntity, UUID> {
    Page<RunEntity> findByStatus(RunEntity.RunStatus status, Pageable pageable);
    Page<RunEntity> findByAuthorId(UUID authorId, Pageable pageable);
}
