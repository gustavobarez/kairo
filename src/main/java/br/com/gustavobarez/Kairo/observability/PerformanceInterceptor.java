package br.com.gustavobarez.Kairo.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PerformanceInterceptor implements HandlerInterceptor {

    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE");
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Autowired
    private MeterRegistry meterRegistry;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE, startTime);

        performanceLogger.debug("Request started: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            String endpoint = request.getRequestURI();
            String method = request.getMethod();
            int statusCode = response.getStatus();

            Timer.Sample sample = Timer.start(meterRegistry);
            sample.stop(Timer.builder("http.request.duration")
                    .tag("endpoint", endpoint)
                    .tag("method", method)
                    .tag("status", String.valueOf(statusCode))
                    .register(meterRegistry));

            meterRegistry.counter("http.requests.total",
                    "endpoint", endpoint,
                    "method", method,
                    "status", String.valueOf(statusCode))
                    .increment();

            if (duration > 1000) {
                performanceLogger.warn("Slow request detected: {} {} took {}ms (Status: {})",
                        method, endpoint, duration, statusCode);
            } else {
                performanceLogger.info("Request completed: {} {} in {}ms (Status: {})",
                        method, endpoint, duration, statusCode);
            }

            if (endpoint.contains("/statistics")) {
                meterRegistry.timer("statistics.response.time").record(duration,
                        java.util.concurrent.TimeUnit.MILLISECONDS);
                performanceLogger.info("Statistics calculation took {}ms", duration);
            }
        }
    }
}