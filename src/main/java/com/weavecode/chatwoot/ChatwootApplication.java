package com.weavecode.chatwoot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application for Chatwoot Multi-tenant Backend
 * 
 * This is the entry point for the Chatwoot backend application.
 * It provides a comprehensive multi-tenant chat platform with:
 * - Multi-tenant architecture with PostgreSQL and Redis
 * - Comprehensive security implementation
 * - Real-time notifications and WebSocket support
 * - Advanced analytics and performance monitoring
 * - Production-ready health checks and metrics
 * - Complete testing suite
 * 
 * @author WeaveCode Team
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication(
    scanBasePackages = "com.weavecode.chatwoot",
    exclude = {
        // Exclude default security auto-configuration to use custom security
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
    }
)
@EntityScan(basePackages = "com.weavecode.chatwoot.entity")
@EnableJpaRepositories(basePackages = "com.weavecode.chatwoot.repository")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class ChatwootApplication {

    /**
     * Main method - Application entry point
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Set system properties for better performance
        System.setProperty("spring.profiles.default", "default");
        System.setProperty("java.net.preferIPv4Stack", "true");
        
        // Start the Spring Boot application
        SpringApplication app = new SpringApplication(ChatwootApplication.class);
        
        // Configure application properties
        app.setDefaultProperties(java.util.Map.of(
            "spring.application.name", "Chatwoot Backend",
            "spring.profiles.active", "default",
            "server.port", "8080"
        ));
        
        // Run the application
        var context = app.run(args);
        
        // Log application startup information
        var env = context.getEnvironment();
        var port = env.getProperty("server.port", "8080");
        var profile = env.getActiveProfiles().length > 0 ? 
            String.join(",", env.getActiveProfiles()) : "default";
        
        System.out.println("🚀 Chatwoot Backend Started Successfully!");
        System.out.println("📍 Application: " + env.getProperty("spring.application.name"));
        System.out.println("🌐 Profile: " + profile);
        System.out.println("🔌 Port: " + port);
        System.out.println("📊 Health Check: http://localhost:" + port + "/actuator/health");
        System.out.println("📈 Metrics: http://localhost:" + port + "/actuator/metrics");
        System.out.println("📚 API Docs: http://localhost:" + port + "/swagger-ui.html");
        System.out.println("🎯 Ready to handle multi-tenant chat operations!");
    }
}
