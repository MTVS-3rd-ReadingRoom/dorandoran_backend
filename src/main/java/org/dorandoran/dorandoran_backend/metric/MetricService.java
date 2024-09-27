package org.dorandoran.dorandoran_backend.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MetricService {
    private final MeterRegistry registry;

    // 카운터를 저장하기 위한 맵
    private final Map<String, Counter> requestCounters = new HashMap<>();
    private final Map<String, Counter> errorCounters = new HashMap<>();

    public MetricService(MeterRegistry registry) {
        this.registry = registry;
    }

    private Counter getOrCreateRequestCounter(String endpoint, String value) {
        String key = endpoint + "_" + value; // 고유한 키 생성
        return requestCounters.computeIfAbsent(key, k -> Counter.builder("api_requests_total")
                .tags("type", "request", "endpoint", endpoint, "value", value)
                .register(registry));
    }

    private Counter getOrCreateErrorCounter(String endpoint, String errorType) {
        String key = endpoint + "_" + errorType; // 고유한 키 생성
        return errorCounters.computeIfAbsent(key, k -> Counter.builder("api_errors_total")
                .tags("type", "error", "endpoint", endpoint, "errorType", errorType)
                .register(registry));
    }

    public void incrementRequestCount(String endpoint, String userRole) {
        Counter counter = getOrCreateRequestCounter(endpoint, userRole);
        counter.increment(); // 요청 수 증가
    }

    public void incrementErrorCount(String endpoint, String errorType) {
        Counter counter = getOrCreateErrorCounter(endpoint, errorType);
        counter.increment(); // 에러 수 증가
    }
}
