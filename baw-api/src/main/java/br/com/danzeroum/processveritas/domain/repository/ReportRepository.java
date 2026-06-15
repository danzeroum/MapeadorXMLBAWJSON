package br.com.danzeroum.processveritas.domain.repository;

import br.com.danzeroum.processveritas.domain.model.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {

    @Query(value = "SELECT r.payload -> :section FROM reports r WHERE r.run_id = :runId",
           nativeQuery = true)
    Optional<String> findSectionByRunId(@Param("runId") UUID runId, @Param("section") String section);
}
