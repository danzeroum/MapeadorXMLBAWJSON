package br.com.danzeroum.processveritas.domain.repository;

import br.com.danzeroum.processveritas.domain.model.SourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SourceRepository extends JpaRepository<SourceEntity, UUID> {
}
