package br.com.danzeroum.processveritas.domain.repository;

import br.com.danzeroum.processveritas.domain.model.AuditLogEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AuditLogRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    AuditLogRepository auditLogRepository;

    @Test
    void savesAndRetrievesIpv4Address() {
        AuditLogEntity log = auditLog("192.168.1.100");

        AuditLogEntity saved = auditLogRepository.save(log);
        AuditLogEntity found = auditLogRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getIpAddress()).isEqualTo("192.168.1.100");
    }

    @Test
    void savesAndRetrievesIpv6Address() {
        AuditLogEntity log = auditLog("2001:db8::1");

        AuditLogEntity saved = auditLogRepository.save(log);
        AuditLogEntity found = auditLogRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getIpAddress()).isEqualTo("2001:db8::1");
    }

    @Test
    void savesAuditLogWithNullIpAddress() {
        AuditLogEntity log = auditLog(null);

        AuditLogEntity saved = auditLogRepository.save(log);
        AuditLogEntity found = auditLogRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getIpAddress()).isNull();
    }

    private AuditLogEntity auditLog(String ipAddress) {
        AuditLogEntity log = new AuditLogEntity();
        log.setActorEmail("test@example.com");
        log.setAction("LOGIN");
        log.setResource("auth");
        log.setIpAddress(ipAddress);
        return log;
    }
}
