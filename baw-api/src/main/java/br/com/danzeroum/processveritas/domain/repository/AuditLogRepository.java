package br.com.danzeroum.processveritas.domain.repository;

import br.com.danzeroum.processveritas.domain.model.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
}
