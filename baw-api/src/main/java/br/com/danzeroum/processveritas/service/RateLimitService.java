package br.com.danzeroum.processveritas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    @Value("${pv.rate-limit.runs-per-hour:10}")
    private int runsPerHour;

    private final ConcurrentHashMap<String, Deque<Instant>> buckets = new ConcurrentHashMap<>();

    public boolean isAllowed(String userKey) {
        Instant cutoff = Instant.now().minusSeconds(3600);
        Deque<Instant> timestamps = buckets.computeIfAbsent(userKey, k -> new ArrayDeque<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst().isBefore(cutoff)) {
                timestamps.pollFirst();
            }
            if (timestamps.size() >= runsPerHour) {
                return false;
            }
            timestamps.addLast(Instant.now());
            return true;
        }
    }
}
