package com.solar.monitoring.system;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@Slf4j
public class MetricsCollector {

    private final MeterRegistry meterRegistry;
    private final AtomicLong totalMemoryUsage = new AtomicLong(0);
    private final AtomicLong heapMemoryUsage = new AtomicLong(0);

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void collectSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        // Update atomic references for gauge metrics
        totalMemoryUsage.set(usedMemory);
        heapMemoryUsage.set(totalMemory);

        // Register gauges if not already registered
        meterRegistry.gauge("jvm.memory.used", totalMemoryUsage);
        meterRegistry.gauge("jvm.memory.total", heapMemoryUsage);
        meterRegistry.gauge("jvm.memory.max", maxMemory);

        log.debug("Collected system metrics - Used: {}MB, Total: {}MB, Max: {}MB",
                usedMemory / 1024 / 1024,
                totalMemory / 1024 / 1024,
                maxMemory / 1024 / 1024);
    }

    @Scheduled(fixedRate = 60000) // Every minute
    public void collectCustomMetrics() {
        // Collect custom business metrics
        meterRegistry.gauge("solar.panels.active", getActivePanelCount());
        meterRegistry.gauge("solar.efficiency.average", getAverageEfficiency());

        log.debug("Collected custom solar metrics");
    }

    private double getActivePanelCount() {
        // Implementation to get active panel count
        return 100.0; // Placeholder
    }

    private double getAverageEfficiency() {
        // Implementation to calculate average efficiency
        return 85.5; // Placeholder
    }

}
