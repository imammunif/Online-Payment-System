package com.dansmultipro.ops.service;

import io.github.bucket4j.Bucket;

public interface RateLimiterService {

    Bucket createNewBucket();

    boolean allowRequest(String email);

    long getRemainingAttempts(String email);

    void resetBucket(String email);

    long getRemainingWaitSeconds(String email);

}
