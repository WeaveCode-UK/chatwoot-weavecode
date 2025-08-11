package com.weavecode.chatwoot.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserPerformanceMetrics {
    
    private UUID userId;
    private String userEmail;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private int totalConversationsHandled;
    private int totalMessagesSent;
    private int totalMessagesReceived;
    private double averageResponseTimeMinutes;
    private int totalResolvedConversations;
    private int totalEscalatedConversations;
    private double customerSatisfactionScore;
    private int totalWorkingHours;
    private double efficiencyRating;
    private int totalTicketsClosed;
    private double firstResponseTimeMinutes;
    private int totalFollowUps;
    private String performanceGrade;
    
    // Constructor
    public UserPerformanceMetrics() {}
    
    // Utility methods
    public double getResolutionRate() {
        if (totalConversationsHandled == 0) return 0.0;
        return (double) totalResolvedConversations / totalConversationsHandled * 100;
    }
    
    public double getEscalationRate() {
        if (totalConversationsHandled == 0) return 0.0;
        return (double) totalEscalatedConversations / totalConversationsHandled * 100;
    }
    
    public double getAverageMessagesPerConversation() {
        if (totalConversationsHandled == 0) return 0.0;
        return (double) (totalMessagesSent + totalMessagesReceived) / totalConversationsHandled;
    }
    
    public boolean isHighPerformer() {
        return customerSatisfactionScore >= 4.5 && efficiencyRating >= 8.0;
    }
    
    public boolean needsImprovement() {
        return customerSatisfactionScore < 3.0 || efficiencyRating < 5.0;
    }
}
