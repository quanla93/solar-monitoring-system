package com.solar.monitoring.system.kafka.exception;

public class KafkaException extends RuntimeException {
    /**
     * Constructs a new KafkaException with the specified detail message.
     *
     * @param message the detail message describing the Kafka-related error
     */
    public KafkaException(String message) {
        super(message);
    }

    /**
     * Constructs a new KafkaException with the specified detail message and cause.
     *
     * This unchecked exception represents a Kafka-related failure and preserves the
     * original throwable as the cause for stack-trace chaining.
     *
     * @param message detailed message describing the failure
     * @param cause the underlying cause of this exception (may be null)
     */
    public KafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}