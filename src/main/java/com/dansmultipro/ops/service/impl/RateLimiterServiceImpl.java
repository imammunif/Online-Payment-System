package com.dansmultipro.ops.service.impl;

import com.dansmultipro.ops.service.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(3, Refill.intervally(3, Duration.ofMinutes(5)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public boolean allowRequest(String email) {
        Bucket bucket = cache.computeIfAbsent(email, k -> createNewBucket());
        return bucket.tryConsume(1);
    }

    @Override
    public long getRemainingAttempts(String email) {
        Bucket bucket = cache.get(email);
        if (bucket == null) {
            return 3;
        }
        return bucket.getAvailableTokens();
    }

    @Override
    public void resetBucket(String username) {
        cache.remove(username);
    }

}
