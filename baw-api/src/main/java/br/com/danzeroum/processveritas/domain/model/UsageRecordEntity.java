package br.com.danzeroum.processveritas.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "usage_records",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "period"}))
public class UsageRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "period", nullable = false)
    private LocalDate period;

    @Column(name = "run_count", nullable = false)
    private int runCount = 0;

    @Column(name = "storage_mb", precision = 10, scale = 2)
    private BigDecimal storageMb;

    public UUID getId() { return id; }
    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }
    public LocalDate getPeriod() { return period; }
    public void setPeriod(LocalDate period) { this.period = period; }
    public int getRunCount() { return runCount; }
    public void setRunCount(int runCount) { this.runCount = runCount; }
    public BigDecimal getStorageMb() { return storageMb; }
    public void setStorageMb(BigDecimal storageMb) { this.storageMb = storageMb; }
}
