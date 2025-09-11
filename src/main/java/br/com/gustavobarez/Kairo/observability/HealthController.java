package br.com.gustavobarez.Kairo.observability;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        long startTime = System.currentTimeMillis();

        Map<String, Object> healthStatus = new HashMap<>();
        healthStatus.put("timestamp", LocalDateTime.now());
        healthStatus.put("status", "UP");
        healthStatus.put("service", "kairo-application");

        try {
            checkDatabase();
            healthStatus.put("database", "UP");
            logger.info("Health check passed - Database connection OK");
        } catch (Exception e) {
            healthStatus.put("database", "DOWN");
            healthStatus.put("error", e.getMessage());
            logger.error("Health check failed - Database connection error: {}", e.getMessage());

            long responseTime = System.currentTimeMillis() - startTime;
            healthStatus.put("responseTimeMs", responseTime);
            return ResponseEntity.status(503).body(healthStatus);
        }

        long responseTime = System.currentTimeMillis() - startTime;
        healthStatus.put("responseTimeMs", responseTime);

        logger.info("Health check completed in {}ms", responseTime);
        return ResponseEntity.ok(healthStatus);
    }

    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, String>> readiness() {
        Map<String, String> status = new HashMap<>();
        try {
            checkDatabase();
            status.put("status", "READY");
            logger.debug("Readiness check passed");
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("status", "NOT_READY");
            status.put("error", e.getMessage());
            logger.error("Readiness check failed: {}", e.getMessage());
            return ResponseEntity.status(503).body(status);
        }
    }

    @GetMapping("/health/live")
    public ResponseEntity<Map<String, String>> liveness() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "ALIVE");
        status.put("timestamp", LocalDateTime.now().toString());
        logger.debug("Liveness check passed");
        return ResponseEntity.ok(status);
    }

    private void checkDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(5)) {
                throw new Exception("Database connection is not valid");
            }
        }
    }
}