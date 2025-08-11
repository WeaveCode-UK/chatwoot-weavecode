package com.weavecode.chatwoot.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuator.info.Info;
import org.springframework.boot.actuator.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChatwootInfoContributor implements InfoContributor {

    @Autowired
    private Environment environment;

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> details = new HashMap<>();
        
        // Application details
        details.put("name", "Chatwoot Multi-Tenant Backend");
        details.put("version", "1.0.0");
        details.put("description", "Enterprise-grade multi-tenant chat system backend");
        details.put("vendor", "WeaveCode");
        details.put("website", "https://weavecode.co.uk");
        
        // Environment details
        details.put("environment", environment.getActiveProfiles().length > 0 ? 
            environment.getActiveProfiles()[0] : "default");
        details.put("javaVersion", System.getProperty("java.version"));
        details.put("javaVendor", System.getProperty("java.vendor"));
        
        // System details
        details.put("osName", System.getProperty("os.name"));
        details.put("osVersion", System.getProperty("os.version"));
        details.put("osArchitecture", System.getProperty("os.arch"));
        
        // Runtime details
        Runtime runtime = Runtime.getRuntime();
        details.put("availableProcessors", runtime.availableProcessors());
        details.put("maxMemory", formatBytes(runtime.maxMemory()));
        details.put("totalMemory", formatBytes(runtime.totalMemory()));
        details.put("freeMemory", formatBytes(runtime.freeMemory()));
        
        // Build details
        details.put("buildTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        details.put("startupTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // Feature flags
        details.put("features", Map.of(
            "multiTenancy", true,
            "realTimeNotifications", true,
            "advancedAnalytics", true,
            "performanceOptimization", true,
            "aiAutomation", true,
            "webSocketSupport", true,
            "redisCaching", true,
            "jwtAuthentication", true,
            "roleBasedAccess", true
        ));
        
        // API endpoints
        details.put("apiEndpoints", Map.of(
            "baseUrl", "/api",
            "actuatorUrl", "/actuator",
            "websocketUrl", "/ws",
            "openApiUrl", "/swagger-ui.html"
        ));
        
        builder.withDetail("chatwoot", details);
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
