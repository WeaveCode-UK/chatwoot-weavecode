package com.weavecode.chatwoot.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ChatwootMetricsCollector {

    private static final Logger logger = LoggerFactory.getLogger(ChatwootMetricsCollector.class);

    private final MeterRegistry meterRegistry;

    // Counters
    private final Counter totalConversationsCounter;
    private final Counter totalMessagesCounter;
    private final Counter totalUsersCounter;
    private final Counter totalTenantsCounter;
    private final Counter authenticationAttemptsCounter;
    private final Counter authenticationFailuresCounter;
    private final Counter apiRequestsCounter;
    private final Counter apiErrorsCounter;
    private final Counter cacheHitsCounter;
    private final Counter cacheMissesCounter;
    private final Counter notificationsSentCounter;
    private final Counter webSocketConnectionsCounter;

    // Timers
    private final Timer apiResponseTimeTimer;
    private final Timer databaseQueryTimer;
    private final Timer cacheOperationTimer;
    private final Timer notificationDeliveryTimer;

    // Gauges
    private final AtomicLong activeConversationsGauge;
    private final AtomicLong activeUsersGauge;
    private final AtomicLong onlineUsersGauge;
    private final AtomicInteger activeWebSocketConnectionsGauge;
    private final AtomicLong totalCacheSizeGauge;
    private final AtomicLong databaseConnectionsGauge;

    @Autowired
    public ChatwootMetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Initialize counters
        this.totalConversationsCounter = Counter.builder("chatwoot.conversations.total")
                .description("Total number of conversations created")
                .register(meterRegistry);

        this.totalMessagesCounter = Counter.builder("chatwoot.messages.total")
                .description("Total number of messages sent")
                .register(meterRegistry);

        this.totalUsersCounter = Counter.builder("chatwoot.users.total")
                .description("Total number of users registered")
                .register(meterRegistry);

        this.totalTenantsCounter = Counter.builder("chatwoot.tenants.total")
                .description("Total number of tenants")
                .register(meterRegistry);

        this.authenticationAttemptsCounter = Counter.builder("chatwoot.auth.attempts")
                .description("Total authentication attempts")
                .register(meterRegistry);

        this.authenticationFailuresCounter = Counter.builder("chatwoot.auth.failures")
                .description("Total authentication failures")
                .register(meterRegistry);

        this.apiRequestsCounter = Counter.builder("chatwoot.api.requests")
                .description("Total API requests")
                .register(meterRegistry);

        this.apiErrorsCounter = Counter.builder("chatwoot.api.errors")
                .description("Total API errors")
                .register(meterRegistry);

        this.cacheHitsCounter = Counter.builder("chatwoot.cache.hits")
                .description("Total cache hits")
                .register(meterRegistry);

        this.cacheMissesCounter = Counter.builder("chatwoot.cache.misses")
                .description("Total cache misses")
                .register(meterRegistry);

        this.notificationsSentCounter = Counter.builder("chatwoot.notifications.sent")
                .description("Total notifications sent")
                .register(meterRegistry);

        this.webSocketConnectionsCounter = Counter.builder("chatwoot.websocket.connections")
                .description("Total WebSocket connections")
                .register(meterRegistry);

        // Initialize timers
        this.apiResponseTimeTimer = Timer.builder("chatwoot.api.response.time")
                .description("API response time")
                .register(meterRegistry);

        this.databaseQueryTimer = Timer.builder("chatwoot.database.query.time")
                .description("Database query execution time")
                .register(meterRegistry);

        this.cacheOperationTimer = Timer.builder("chatwoot.cache.operation.time")
                .description("Cache operation time")
                .register(meterRegistry);

        this.notificationDeliveryTimer = Timer.builder("chatwoot.notifications.delivery.time")
                .description("Notification delivery time")
                .register(meterRegistry);

        // Initialize gauges
        this.activeConversationsGauge = new AtomicLong(0);
        this.activeUsersGauge = new AtomicLong(0);
        this.onlineUsersGauge = new AtomicLong(0);
        this.activeWebSocketConnectionsGauge = new AtomicInteger(0);
        this.totalCacheSizeGauge = new AtomicLong(0);
        this.databaseConnectionsGauge = new AtomicLong(0);

        // Register gauges
        Gauge.builder("chatwoot.conversations.active")
                .description("Number of active conversations")
                .register(meterRegistry, activeConversationsGauge, AtomicLong::get);

        Gauge.builder("chatwoot.users.active")
                .description("Number of active users")
                .register(meterRegistry, activeUsersGauge, AtomicLong::get);

        Gauge.builder("chatwoot.users.online")
                .description("Number of online users")
                .register(meterRegistry, onlineUsersGauge, AtomicLong::get);

        Gauge.builder("chatwoot.websocket.connections.active")
                .description("Number of active WebSocket connections")
                .register(meterRegistry, activeWebSocketConnectionsGauge, AtomicInteger::get);

        Gauge.builder("chatwoot.cache.size")
                .description("Total cache size")
                .register(meterRegistry, totalCacheSizeGauge, AtomicLong::get);

        Gauge.builder("chatwoot.database.connections")
                .description("Number of database connections")
                .register(meterRegistry, databaseConnectionsGauge, AtomicLong::get);

        logger.info("Chatwoot metrics collector initialized");
    }

    // Counter methods
    public void incrementConversations() {
        totalConversationsCounter.increment();
    }

    public void incrementMessages() {
        totalMessagesCounter.increment();
    }

    public void incrementUsers() {
        totalUsersCounter.increment();
    }

    public void incrementTenants() {
        totalTenantsCounter.increment();
    }

    public void incrementAuthenticationAttempts() {
        authenticationAttemptsCounter.increment();
    }

    public void incrementAuthenticationFailures() {
        authenticationFailuresCounter.increment();
    }

    public void incrementApiRequests() {
        apiRequestsCounter.increment();
    }

    public void incrementApiErrors() {
        apiErrorsCounter.increment();
    }

    public void incrementCacheHits() {
        cacheHitsCounter.increment();
    }

    public void incrementCacheMisses() {
        cacheMissesCounter.increment();
    }

    public void incrementNotificationsSent() {
        notificationsSentCounter.increment();
    }

    public void incrementWebSocketConnections() {
        webSocketConnectionsCounter.increment();
    }

    // Timer methods
    public Timer.Sample startApiResponseTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopApiResponseTimer(Timer.Sample sample) {
        sample.stop(apiResponseTimeTimer);
    }

    public Timer.Sample startDatabaseQueryTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopDatabaseQueryTimer(Timer.Sample sample) {
        sample.stop(databaseQueryTimer);
    }

    public Timer.Sample startCacheOperationTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopCacheOperationTimer(Timer.Sample sample) {
        sample.stop(cacheOperationTimer);
    }

    public Timer.Sample startNotificationDeliveryTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopNotificationDeliveryTimer(Timer.Sample sample) {
        sample.stop(notificationDeliveryTimer);
    }

    // Gauge methods
    public void setActiveConversations(long count) {
        activeConversationsGauge.set(count);
    }

    public void setActiveUsers(long count) {
        activeUsersGauge.set(count);
    }

    public void setOnlineUsers(long count) {
        onlineUsersGauge.set(count);
    }

    public void setActiveWebSocketConnections(int count) {
        activeWebSocketConnectionsGauge.set(count);
    }

    public void setTotalCacheSize(long size) {
        totalCacheSizeGauge.set(size);
    }

    public void setDatabaseConnections(long count) {
        databaseConnectionsGauge.set(count);
    }

    // Scheduled metrics update
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void updateMetrics() {
        try {
            // Update system metrics
            updateSystemMetrics();
            
            // Update business metrics
            updateBusinessMetrics();
            
            logger.debug("Metrics updated successfully");
        } catch (Exception e) {
            logger.error("Error updating metrics: {}", e.getMessage(), e);
        }
    }

    private void updateSystemMetrics() {
        // Update system-related gauges
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // These would be updated with actual values from the system
        setTotalCacheSize(usedMemory / 1024); // Convert to KB for demo
        setDatabaseConnections(5 + (long) (Math.random() * 10)); // Simulated value
    }

    private void updateBusinessMetrics() {
        // Update business-related gauges
        // These would be updated with actual values from repositories/services
        
        // Simulated values for demo
        setActiveConversations(10 + (long) (Math.random() * 50));
        setActiveUsers(20 + (long) (Math.random() * 100));
        setOnlineUsers(5 + (long) (Math.random() * 20));
        setActiveWebSocketConnections(3 + (int) (Math.random() * 10));
    }

    // Utility methods for external metric updates
    public void recordApiRequest(String endpoint, String method, int statusCode) {
        incrementApiRequests();
        
        if (statusCode >= 400) {
            incrementApiErrors();
        }
        
        // Record endpoint-specific metrics
        Counter.builder("chatwoot.api.requests.by.endpoint")
                .tag("endpoint", endpoint)
                .tag("method", method)
                .tag("status", String.valueOf(statusCode))
                .register(meterRegistry)
                .increment();
    }

    public void recordCacheOperation(String operation, String key, boolean hit) {
        if (hit) {
            incrementCacheHits();
        } else {
            incrementCacheMisses();
        }
        
        // Record operation-specific metrics
        Counter.builder("chatwoot.cache.operations.by.type")
                .tag("operation", operation)
                .tag("result", hit ? "hit" : "miss")
                .register(meterRegistry)
                .increment();
    }

    public void recordNotificationDelivery(String type, String channel, boolean success) {
        if (success) {
            incrementNotificationsSent();
        }
        
        // Record delivery-specific metrics
        Counter.builder("chatwoot.notifications.delivery.by.type")
                .tag("type", type)
                .tag("channel", channel)
                .tag("status", success ? "success" : "failed")
                .register(meterRegistry)
                .increment();
    }
}
