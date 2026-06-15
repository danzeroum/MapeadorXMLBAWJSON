package br.com.danzeroum.processveritas.domain.repository;

import br.com.danzeroum.processveritas.domain.model.UsageRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsageRecordRepository extends JpaRepository<UsageRecordEntity, UUID> {
    List<UsageRecordEntity> findByUser_IdOrderByPeriodDesc(UUID userId);
    List<UsageRecordEntity> findByPeriodBetweenOrderByPeriodDesc(LocalDate from, LocalDate to);
    Optional<UsageRecordEntity> findByUser_IdAndPeriod(UUID userId, LocalDate period);
}
