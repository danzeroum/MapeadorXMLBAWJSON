package br.com.danzeroum.processveritas.service;

import br.com.danzeroum.processveritas.domain.model.UsageRecordEntity;
import br.com.danzeroum.processveritas.domain.repository.UsageRecordRepository;
import br.com.danzeroum.processveritas.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class UsageService {

    private static final Logger log = LoggerFactory.getLogger(UsageService.class);

    @Autowired
    private UsageRecordRepository usageRepo;

    @Autowired
    private UserRepository userRepo;

    public List<UsageRecordEntity> listByPeriod(LocalDate from, LocalDate to) {
        return usageRepo.findByPeriodBetweenOrderByPeriodDesc(from, to);
    }

    public List<UsageRecordEntity> listForUser(UUID userId) {
        return usageRepo.findByUser_IdOrderByPeriodDesc(userId);
    }

    @Transactional
    public void incrementRunCount(UUID userId) {
        LocalDate period = LocalDate.now().withDayOfMonth(1);
        UsageRecordEntity record = usageRepo.findByUser_IdAndPeriod(userId, period)
                .orElseGet(() -> {
                    UsageRecordEntity r = new UsageRecordEntity();
                    userRepo.findById(userId).ifPresent(r::setUser);
                    r.setPeriod(period);
                    return r;
                });
        record.setRunCount(record.getRunCount() + 1);
        usageRepo.save(record);
        log.debug("Run count incremented for userId={} period={}", userId, period);
    }
}
