package br.com.danzeroum.processveritas.domain.repository;

import br.com.danzeroum.processveritas.domain.model.MethodologyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MethodologyRepository extends JpaRepository<MethodologyEntity, UUID> {
    Optional<MethodologyEntity> findByActiveTrue();
    Page<MethodologyEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
