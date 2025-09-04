package com.solar.monitoring.system.redis.exception;

public class RedisException extends RuntimeException {
    /**
     * Constructs a new RedisException with the specified detail message.
     *
     * @param message the detail message describing the Redis error
     */
    public RedisException(String message) {
        super(message);
    }

    /**
     * Constructs a new RedisException with the specified detail message and cause.
     *
     * @param message the detail message describing the Redis-related error (may be {@code null})
     * @param cause   the underlying cause of this exception (may be {@code null})
     */
    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }
}