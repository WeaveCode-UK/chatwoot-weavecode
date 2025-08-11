package com.weavecode.chatwoot.enums;

public enum NotificationType {
    // Conversation related
    NEW_CONVERSATION("New conversation started"),
    CONVERSATION_ASSIGNED("Conversation assigned to you"),
    CONVERSATION_RESOLVED("Conversation resolved"),
    CONVERSATION_REOPENED("Conversation reopened"),
    
    // Message related
    NEW_MESSAGE("New message received"),
    MESSAGE_READ("Message marked as read"),
    MESSAGE_EDITED("Message edited"),
    
    // Customer related
    CUSTOMER_ONLINE("Customer is online"),
    CUSTOMER_OFFLINE("Customer went offline"),
    CUSTOMER_TYPING("Customer is typing"),
    
    // System related
    SYSTEM_MAINTENANCE("System maintenance scheduled"),
    SYSTEM_UPDATE("System update available"),
    PERFORMANCE_ALERT("Performance alert"),
    
    // User related
    USER_LOGIN("User logged in"),
    USER_LOGOUT("User logged out"),
    USER_STATUS_CHANGE("User status changed"),
    
    // Automation related
    AUTOMATION_TRIGGERED("Automation triggered"),
    AI_SUGGESTION("AI suggestion available"),
    
    // Billing related
    SUBSCRIPTION_EXPIRING("Subscription expiring soon"),
    PAYMENT_FAILED("Payment failed"),
    PLAN_UPGRADE("Plan upgrade available");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
