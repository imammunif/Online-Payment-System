package com.dansmultipro.ops.exception;

public class RateLimiterException extends RuntimeException {

    private long remainingWaitSeconds;

    public RateLimiterException(String message) {
        super(message);
        this.remainingWaitSeconds = 0;
    }

    public RateLimiterException(String message, long remainingWaitSeconds) {
        super(message);
        this.remainingWaitSeconds = remainingWaitSeconds;
    }

    public long getRemainingWaitSeconds() {
        return remainingWaitSeconds;
    }

    public void setRemainingWaitSeconds(long remainingWaitSeconds) {
        this.remainingWaitSeconds = remainingWaitSeconds;
    }

}
