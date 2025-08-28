package com.solar.monitoring.system.redis.util;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisKeyUtil {

    private static final String REALTIME_DATA_PREFIX = "realtime:machine:";
    private static final String MACHINE_STATUS_PREFIX = "status:machine:";
    private static final String ALERT_PREFIX = "alert:machine:";

    public static String getRealtimeDataKey(String machineId) {
        return REALTIME_DATA_PREFIX + machineId;
    }

    public static String getMachineStatusKey(String machineId) {
        return MACHINE_STATUS_PREFIX + machineId;
    }

    public static String getAlertKey(String machineId) {
        return ALERT_PREFIX + machineId;
    }

    public static String getMachineIdFromKey(String key, String prefix) {
        if (key.startsWith(prefix)) {
            return key.substring(prefix.length());
        }
        return null;
    }

}
