package com.weavecode.chatwoot.logging;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders;
import net.logstash.logback.composite.loggingevent.MdcJsonProvider;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;
import net.logstash.logback.composite.loggingevent.TimestampJsonProvider;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class StructuredLoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(StructuredLoggingConfig.class);

    @Value("${logging.file.path:logs}")
    private String logFilePath;

    @Value("${logging.file.name:chatwoot}")
    private String logFileName;

    @Value("${logging.level.root:INFO}")
    private String rootLogLevel;

    @Value("${logging.level.com.weavecode.chatwoot:DEBUG}")
    private String appLogLevel;

    @PostConstruct
    public void configureLogging() {
        logger.info("Configuring structured logging for Chatwoot backend");
        
        try {
            // Configure logback programmatically
            configureLogback();
            
            logger.info("Structured logging configured successfully");
        } catch (Exception e) {
            logger.error("Failed to configure structured logging: {}", e.getMessage(), e);
        }
    }

    private void configureLogback() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        
        // Clear existing appenders
        context.getLoggerList().forEach(logger -> logger.detachAndStopAllAppenders());
        
        // Configure console appender with JSON format
        ConsoleAppender<ILoggingEvent> consoleAppender = createConsoleAppender(context);
        
        // Configure file appender with JSON format
        RollingFileAppender<ILoggingEvent> fileAppender = createFileAppender(context);
        
        // Get root logger and add appenders
        ch.qos.logback.classic.Logger rootLogger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(consoleAppender);
        rootLogger.addAppender(fileAppender);
        rootLogger.setLevel(ch.qos.logback.classic.Level.valueOf(rootLogLevel));
        
        // Configure application logger
        ch.qos.logback.classic.Logger appLogger = context.getLogger("com.weavecode.chatwoot");
        appLogger.setLevel(ch.qos.logback.classic.Level.valueOf(appLogLevel));
        appLogger.setAdditive(false);
        appLogger.addAppender(consoleAppender);
        appLogger.addAppender(fileAppender);
    }

    private ConsoleAppender<ILoggingEvent> createConsoleAppender(LoggerContext context) {
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setContext(context);
        appender.setName("console");
        
        // Create JSON encoder
        LoggingEventCompositeJsonEncoder encoder = new LoggingEventCompositeJsonEncoder();
        encoder.setContext(context);
        
        // Configure JSON providers
        LoggingEventJsonProviders providers = new LoggingEventJsonProviders();
        
        // Timestamp provider
        TimestampJsonProvider timestampProvider = new TimestampJsonProvider();
        timestampProvider.setFieldName("timestamp");
        timestampProvider.setTimeZone("UTC");
        providers.addProvider(timestampProvider);
        
        // Message provider
        MessageJsonProvider messageProvider = new MessageJsonProvider();
        providers.addProvider(messageProvider);
        
        // MDC provider for correlation IDs and other context
        MdcJsonProvider mdcProvider = new MdcJsonProvider();
        mdcProvider.setIncludeMdcKeyName(true);
        providers.addProvider(mdcProvider);
        
        encoder.setProviders(providers);
        encoder.start();
        
        appender.setEncoder(encoder);
        appender.start();
        
        return appender;
    }

    private RollingFileAppender<ILoggingEvent> createFileAppender(LoggerContext context) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);
        appender.setName("file");
        appender.setFile(logFilePath + "/" + logFileName + ".log");
        
        // Configure rolling policy
        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setFileNamePattern(logFilePath + "/" + logFileName + ".%d{yyyy-MM-dd}.%i.log.gz");
        rollingPolicy.setMaxHistory(30); // Keep 30 days of logs
        rollingPolicy.setTotalSizeCap(1024 * 1024 * 1024); // 1GB total size cap
        rollingPolicy.setParent(appender);
        rollingPolicy.start();
        
        appender.setRollingPolicy(rollingPolicy);
        
        // Create JSON encoder (same as console)
        LoggingEventCompositeJsonEncoder encoder = new LoggingEventCompositeJsonEncoder();
        encoder.setContext(context);
        
        LoggingEventJsonProviders providers = new LoggingEventJsonProviders();
        
        TimestampJsonProvider timestampProvider = new TimestampJsonProvider();
        timestampProvider.setFieldName("timestamp");
        timestampProvider.setTimeZone("UTC");
        providers.addProvider(timestampProvider);
        
        MessageJsonProvider messageProvider = new MessageJsonProvider();
        providers.addProvider(messageProvider);
        
        MdcJsonProvider mdcProvider = new MdcJsonProvider();
        mdcProvider.setIncludeMdcKeyName(true);
        providers.addProvider(mdcProvider);
        
        encoder.setProviders(providers);
        encoder.start();
        
        appender.setEncoder(encoder);
        appender.start();
        
        return appender;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }
}
