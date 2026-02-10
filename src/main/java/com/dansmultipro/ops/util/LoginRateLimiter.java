package com.dansmultipro.ops.util;

import io.github.bucket4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final Map<String, Integer> burstMap = new ConcurrentHashMap<>();
    private final Map<String, Instant> penaltyMap = new ConcurrentHashMap<>();

    public boolean allowRequest(String ip) {
        Bucket bucket = resolveBucket(ip);
        return bucket.tryConsume(1);
    }

    private Bucket resolveBucket(String ip) {
        return cache.computeIfAbsent(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        Refill refill = Refill.intervally(5, Duration.ofMinutes(3));
        Bandwidth limit = Bandwidth.classic(5, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public void extendRefill(String ip, Duration newDuration) {
        Refill extendRefill = Refill.intervally(5, newDuration);
        Bucket bucket = resolveBucket(ip);
        Bandwidth newBandwidth = Bandwidth.classic(5, extendRefill);

        BucketConfiguration newConfig = BucketConfiguration.builder()
                .addLimit(newBandwidth)
                .build();

        bucket.replaceConfiguration(newConfig, TokensInheritanceStrategy.AS_IS);
    }

    public Duration getNewDuration(String ip) {
        Instant now = Instant.now();
        Instant penaltyExpiresAt = penaltyMap.get(ip);

        if (penaltyExpiresAt != null && now.isBefore(penaltyExpiresAt)) {
            int currentBurst = burstMap.getOrDefault(ip, 1);
            return Duration.ofMinutes(3).multipliedBy(currentBurst);
        }

        Integer burstCount = burstMap.compute(ip, (key, value) -> value == null ? 1 : value + 1);
        Duration base = Duration.ofMinutes(3);
        Duration newDuration = base.multipliedBy(burstCount);
        penaltyMap.put(ip, now.plus(newDuration));

        return newDuration;
    }

    public void resetBucket(String ip) {
        cache.remove(ip);
        burstMap.remove(ip);
        penaltyMap.remove(ip);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledReset() {
        cache.clear();
        burstMap.clear();
        penaltyMap.clear();
    }

}
