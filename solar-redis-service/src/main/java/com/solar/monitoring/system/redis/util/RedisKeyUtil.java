package com.solar.monitoring.system.redis.util;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisKeyUtil {

    private static final String REALTIME_DATA_PREFIX = "realtime:machine:";
    private static final String MACHINE_STATUS_PREFIX = "status:machine:";
    private static final String ALERT_PREFIX = "alert:machine:";

    /**
     * Build the Redis key for a machine's realtime data.
     *
     * @param machineId the machine identifier to append to the realtime data prefix
     * @return the Redis key, formatted as {@code realtime:machine:{machineId}}
     */
    public static String getRealtimeDataKey(String machineId) {
        return REALTIME_DATA_PREFIX + machineId;
    }

    /**
     * Builds the Redis key for a machine's status by prefixing the configured status namespace.
     *
     * @param machineId the machine identifier to append to the status key prefix
     * @return the Redis key for the given machine's status (prefix + machineId)
     */
    public static String getMachineStatusKey(String machineId) {
        return MACHINE_STATUS_PREFIX + machineId;
    }

    /**
     * Builds the Redis key used to store alerts for a specific machine.
     *
     * The key is the alert prefix followed by the provided machine identifier,
     * e.g. "alert:machine:{machineId}".
     *
     * @param machineId the machine identifier to append to the alert key prefix
     * @return the Redis alert key for the given machine
     */
    public static String getAlertKey(String machineId) {
        return ALERT_PREFIX + machineId;
    }

    /**
     * Extracts the machineId from a Redis key if the key begins with the given prefix.
     *
     * @param key the Redis key to parse (e.g. "realtime:machine:123")
     * @param prefix the expected prefix to strip from the key (e.g. "realtime:machine:")
     * @return the substring after the prefix (the machineId) when `key` starts with `prefix`, or {@code null} otherwise
     */
    public static String getMachineIdFromKey(String key, String prefix) {
        if (key.startsWith(prefix)) {
            return key.substring(prefix.length());
        }
        return null;
    }

}
