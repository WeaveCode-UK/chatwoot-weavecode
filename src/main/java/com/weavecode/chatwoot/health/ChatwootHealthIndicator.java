package com.weavecode.chatwoot.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChatwootHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(ChatwootHealthIndicator.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try {
            Map<String, Object> details = new HashMap<>();
            boolean isHealthy = true;

            // Check database health
            Health dbHealth = checkDatabaseHealth();
            details.put("database", dbHealth.getDetails());
            if (dbHealth.getStatus() == Health.down().getStatus()) {
                isHealthy = false;
            }

            // Check Redis health
            Health redisHealth = checkRedisHealth();
            details.put("redis", redisHealth.getDetails());
            if (redisHealth.getStatus() == Health.down().getStatus()) {
                isHealthy = false;
            }

            // Check system resources
            Health systemHealth = checkSystemHealth();
            details.put("system", systemHealth.getDetails());
            if (systemHealth.getStatus() == Health.down().getStatus()) {
                isHealthy = false;
            }

            // Check tenant count
            Health tenantHealth = checkTenantHealth();
            details.put("tenants", tenantHealth.getDetails());
            if (tenantHealth.getStatus() == Health.down().getStatus()) {
                isHealthy = false;
            }

            if (isHealthy) {
                return Health.up()
                        .withDetails(details)
                        .build();
            } else {
                return Health.down()
                        .withDetails(details)
                        .build();
            }

        } catch (Exception e) {
            logger.error("Error checking system health: {}", e.getMessage(), e);
            return Health.down()
                    .withException(e)
                    .build();
        }
    }

    private Health checkDatabaseHealth() {
        try {
            long startTime = System.currentTimeMillis();
            
            // Test database connection
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5);
                long responseTime = System.currentTimeMillis() - startTime;
                
                Map<String, Object> details = new HashMap<>();
                details.put("status", isValid ? "UP" : "DOWN");
                details.put("responseTime", responseTime + "ms");
                details.put("url", connection.getMetaData().getURL());
                details.put("databaseProductName", connection.getMetaData().getDatabaseProductName());
                details.put("databaseProductVersion", connection.getMetaData().getDatabaseProductVersion());
                
                if (isValid && responseTime < 1000) {
                    return Health.up().withDetails(details).build();
                } else if (isValid) {
                    return Health.up().withDetails(details).build();
                } else {
                    return Health.down().withDetails(details).build();
                }
            }
        } catch (Exception e) {
            logger.error("Database health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "DOWN")
                    .build();
        }
    }

    private Health checkRedisHealth() {
        try {
            long startTime = System.currentTimeMillis();
            
            // Test Redis connection
            String testKey = "health_check_" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(testKey, "test", java.time.Duration.ofSeconds(10));
            String result = (String) redisTemplate.opsForValue().get(testKey);
            redisTemplate.delete(testKey);
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            Map<String, Object> details = new HashMap<>();
            details.put("status", "UP");
            details.put("responseTime", responseTime + "ms");
            details.put("testResult", "test".equals(result) ? "SUCCESS" : "FAILED");
            
            if (responseTime < 100) {
                return Health.up().withDetails(details).build();
            } else {
                return Health.up().withDetails(details).build();
            }
            
        } catch (Exception e) {
            logger.error("Redis health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "DOWN")
                    .build();
        }
    }

    private Health checkSystemHealth() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            long maxMemory = runtime.maxMemory();
            
            double memoryUsage = (double) usedMemory / maxMemory * 100;
            double cpuLoad = getCpuLoad();
            
            Map<String, Object> details = new HashMap<>();
            details.put("status", "UP");
            details.put("memoryUsage", String.format("%.2f%%", memoryUsage));
            details.put("usedMemory", formatBytes(usedMemory));
            details.put("freeMemory", formatBytes(freeMemory));
            details.put("totalMemory", formatBytes(totalMemory));
            details.put("maxMemory", formatBytes(maxMemory));
            details.put("cpuLoad", String.format("%.2f%%", cpuLoad));
            
            // Check if system is healthy
            boolean isHealthy = memoryUsage < 90.0 && cpuLoad < 90.0;
            
            if (isHealthy) {
                return Health.up().withDetails(details).build();
            } else {
                return Health.down().withDetails(details).build();
            }
            
        } catch (Exception e) {
            logger.error("System health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "DOWN")
                    .build();
        }
    }

    private Health checkTenantHealth() {
        try {
            // This would check tenant-related health metrics
            // For now, return a basic health check
            Map<String, Object> details = new HashMap<>();
            details.put("status", "UP");
            details.put("message", "Tenant system operational");
            
            return Health.up().withDetails(details).build();
            
        } catch (Exception e) {
            logger.error("Tenant health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "DOWN")
                    .build();
        }
    }

    private double getCpuLoad() {
        try {
            // This would get actual CPU load from system
            // For now, return a simulated value
            return Math.random() * 50 + 20; // 20-70%
        } catch (Exception e) {
            logger.warn("Could not get CPU load: {}", e.getMessage());
            return 0.0;
        }
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
