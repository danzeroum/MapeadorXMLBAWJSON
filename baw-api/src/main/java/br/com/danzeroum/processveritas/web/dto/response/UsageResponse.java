package br.com.danzeroum.processveritas.web.dto.response;

import br.com.danzeroum.processveritas.domain.model.UsageRecordEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class UsageResponse {

    private UUID id;
    private UUID userId;
    private LocalDate period;
    private int runCount;
    private BigDecimal storageMb;

    public static UsageResponse from(UsageRecordEntity e) {
        UsageResponse r = new UsageResponse();
        r.id = e.getId();
        r.userId = e.getUser() != null ? e.getUser().getId() : null;
        r.period = e.getPeriod();
        r.runCount = e.getRunCount();
        r.storageMb = e.getStorageMb();
        return r;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public LocalDate getPeriod() { return period; }
    public int getRunCount() { return runCount; }
    public BigDecimal getStorageMb() { return storageMb; }
}
