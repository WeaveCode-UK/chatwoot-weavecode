package com.weavecode.chatwoot.entity;

import com.weavecode.chatwoot.enums.PlanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Subscription entity representing tenant subscription plans
 * Implements enterprise-grade billing patterns and audit trails
 * 
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Entity
@Table(name = "subscriptions")
public class Subscription {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @NotNull(message = "Tenant ID is required")
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @NotNull(message = "Plan type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;
    
    @NotNull(message = "Plan name is required")
    @Size(max = 100, message = "Plan name cannot exceed 100 characters")
    @Column(name = "plan_name", nullable = false)
    private String planName;
    
    @NotNull(message = "Billing cycle is required")
    @Size(max = 20, message = "Billing cycle cannot exceed 20 characters")
    @Column(name = "billing_cycle", nullable = false)
    private String billingCycle = "monthly"; // monthly, quarterly, yearly
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private Double amount;
    
    @NotNull(message = "Currency is required")
    @Size(max = 3, message = "Currency cannot exceed 3 characters")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "GBP";
    
    @Column(name = "setup_fee", precision = 10, scale = 2)
    private Double setupFee;
    
    @Column(name = "discount_percentage")
    private Double discountPercentage;
    
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private Double discountAmount;
    
    @Column(name = "tax_percentage")
    private Double taxPercentage;
    
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private Double taxAmount;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private Double totalAmount;
    
    @Column(name = "features", columnDefinition = "jsonb")
    private String features; // JSON string for plan features
    
    @Column(name = "limits", columnDefinition = "jsonb")
    private String limits; // JSON string for plan limits
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // JSON string for additional subscription data
    
    @Column(name = "status", length = 20, default = "active")
    private String status = "active"; // active, suspended, cancelled, expired
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "trial_start_date")
    private LocalDateTime trialStartDate;
    
    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;
    
    @Column(name = "trial_days")
    private Integer trialDays = 0;
    
    @Column(name = "next_billing_date")
    private LocalDateTime nextBillingDate;
    
    @Column(name = "last_billing_date")
    private LocalDateTime lastBillingDate;
    
    @Column(name = "auto_renew")
    private Boolean autoRenew = true;
    
    @Column(name = "cancellation_date")
    private LocalDateTime cancellationDate;
    
    @Column(name = "cancelled_by")
    private UUID cancelledBy;
    
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
    
    @Column(name = "grace_period_days")
    private Integer gracePeriodDays = 7;
    
    @Column(name = "grace_period_end")
    private LocalDateTime gracePeriodEnd;
    
    @Column(name = "payment_method_id", length = 100)
    private String paymentMethodId;
    
    @Column(name = "payment_provider", length = 50)
    private String paymentProvider; // stripe, paypal
    
    @Column(name = "payment_status", length = 20, default = "pending")
    private String paymentStatus = "pending"; // pending, paid, failed, refunded
    
    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate;
    
    @Column(name = "last_payment_amount", precision = 10, scale = 2)
    private Double lastPaymentAmount;
    
    @Column(name = "failed_payment_count")
    private Integer failedPaymentCount = 0;
    
    @Column(name = "max_failed_payments")
    private Integer maxFailedPayments = 3;
    
    @Column(name = "last_failed_payment_date")
    private LocalDateTime lastFailedPaymentDate;
    
    @Column(name = "last_failed_payment_reason", length = 500)
    private String lastFailedPaymentReason;
    
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private Double refundAmount;
    
    @Column(name = "refund_date")
    private LocalDateTime refundDate;
    
    @Column(name = "refund_reason", length = 500)
    private String refundReason;
    
    @Column(name = "invoice_id", length = 100)
    private String invoiceId;
    
    @Column(name = "invoice_url", length = 500)
    private String invoiceUrl;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Column(name = "is_archived")
    private Boolean isArchived = false;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    @Column(name = "archived_by")
    private UUID archivedBy;
    
    @Column(name = "archive_reason", length = 500)
    private String archiveReason;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Subscription() {}
    
    public Subscription(UUID tenantId, PlanType planType, String planName, Double amount) {
        this.tenantId = tenantId;
        this.planType = planType;
        this.planName = planName;
        this.amount = amount;
        this.startDate = LocalDateTime.now();
        this.nextBillingDate = calculateNextBillingDate();
        calculateTotalAmount();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }
    
    public PlanType getPlanType() {
        return planType;
    }
    
    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }
    
    public String getPlanName() {
        return planName;
    }
    
    public void setPlanName(String planName) {
        this.planName = planName;
    }
    
    public String getBillingCycle() {
        return billingCycle;
    }
    
    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
        this.nextBillingDate = calculateNextBillingDate();
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
        calculateTotalAmount();
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Double getSetupFee() {
        return setupFee;
    }
    
    public void setSetupFee(Double setupFee) {
        this.setupFee = setupFee;
        calculateTotalAmount();
    }
    
    public Double getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(Double discountPercentage) {
        if (discountPercentage != null && (discountPercentage < 0.0 || discountPercentage > 100.0)) {
            throw new IllegalArgumentException("Discount percentage must be between 0.0 and 100.0");
        }
        this.discountPercentage = discountPercentage;
        calculateTotalAmount();
    }
    
    public Double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(Double discountAmount) {
        if (discountAmount != null && discountAmount < 0.0) {
            throw new IllegalArgumentException("Discount amount cannot be negative");
        }
        this.discountAmount = discountAmount;
        calculateTotalAmount();
    }
    
    public Double getTaxPercentage() {
        return taxPercentage;
    }
    
    public void setTaxPercentage(Double taxPercentage) {
        if (taxPercentage != null && taxPercentage < 0.0) {
            throw new IllegalArgumentException("Tax percentage cannot be negative");
        }
        this.taxPercentage = taxPercentage;
        calculateTotalAmount();
    }
    
    public Double getTaxAmount() {
        return taxAmount;
    }
    
    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getFeatures() {
        return features;
    }
    
    public void setFeatures(String features) {
        this.features = features;
    }
    
    public String getLimits() {
        return limits;
    }
    
    public void setLimits(String limits) {
        this.limits = limits;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        updateStatusTimestamps(status);
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public LocalDateTime getTrialStartDate() {
        return trialStartDate;
    }
    
    public void setTrialStartDate(LocalDateTime trialStartDate) {
        this.trialStartDate = trialStartDate;
    }
    
    public LocalDateTime getTrialEndDate() {
        return trialEndDate;
    }
    
    public void setTrialEndDate(LocalDateTime trialEndDate) {
        this.trialEndDate = trialEndDate;
    }
    
    public Integer getTrialDays() {
        return trialDays;
    }
    
    public void setTrialDays(Integer trialDays) {
        this.trialDays = trialDays;
    }
    
    public LocalDateTime getNextBillingDate() {
        return nextBillingDate;
    }
    
    public void setNextBillingDate(LocalDateTime nextBillingDate) {
        this.nextBillingDate = nextBillingDate;
    }
    
    public LocalDateTime getLastBillingDate() {
        return lastBillingDate;
    }
    
    public void setLastBillingDate(LocalDateTime lastBillingDate) {
        this.lastBillingDate = lastBillingDate;
    }
    
    public Boolean getAutoRenew() {
        return autoRenew;
    }
    
    public void setAutoRenew(Boolean autoRenew) {
        this.autoRenew = autoRenew;
    }
    
    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }
    
    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.cancellationDate = cancellationDate;
    }
    
    public UUID getCancelledBy() {
        return cancelledBy;
    }
    
    public void setCancelledBy(UUID cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public Integer getGracePeriodDays() {
        return gracePeriodDays;
    }
    
    public void setGracePeriodDays(Integer gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }
    
    public LocalDateTime getGracePeriodEnd() {
        return gracePeriodEnd;
    }
    
    public void setGracePeriodEnd(LocalDateTime gracePeriodEnd) {
        this.gracePeriodEnd = gracePeriodEnd;
    }
    
    public String getPaymentMethodId() {
        return paymentMethodId;
    }
    
    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    
    public String getPaymentProvider() {
        return paymentProvider;
    }
    
    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        updatePaymentTimestamps(paymentStatus);
    }
    
    public LocalDateTime getLastPaymentDate() {
        return lastPaymentDate;
    }
    
    public void setLastPaymentDate(LocalDateTime lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
    
    public Double getLastPaymentAmount() {
        return lastPaymentAmount;
    }
    
    public void setLastPaymentAmount(Double lastPaymentAmount) {
        this.lastPaymentAmount = lastPaymentAmount;
    }
    
    public Integer getFailedPaymentCount() {
        return failedPaymentCount;
    }
    
    public void setFailedPaymentCount(Integer failedPaymentCount) {
        this.failedPaymentCount = failedPaymentCount;
    }
    
    public Integer getMaxFailedPayments() {
        return maxFailedPayments;
    }
    
    public void setMaxFailedPayments(Integer maxFailedPayments) {
        this.maxFailedPayments = maxFailedPayments;
    }
    
    public LocalDateTime getLastFailedPaymentDate() {
        return lastFailedPaymentDate;
    }
    
    public void setLastFailedPaymentDate(LocalDateTime lastFailedPaymentDate) {
        this.lastFailedPaymentDate = lastFailedPaymentDate;
    }
    
    public String getLastFailedPaymentReason() {
        return lastFailedPaymentReason;
    }
    
    public void setLastFailedPaymentReason(String lastFailedPaymentReason) {
        this.lastFailedPaymentReason = lastFailedPaymentReason;
    }
    
    public Double getRefundAmount() {
        return refundAmount;
    }
    
    public void setRefundAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }
    
    public LocalDateTime getRefundDate() {
        return refundDate;
    }
    
    public void setRefundDate(LocalDateTime refundDate) {
        this.refundDate = refundDate;
    }
    
    public String getRefundReason() {
        return refundReason;
    }
    
    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }
    
    public String getInvoiceId() {
        return invoiceId;
    }
    
    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
    
    public String getInvoiceUrl() {
        return invoiceUrl;
    }
    
    public void setInvoiceUrl(String invoiceUrl) {
        this.invoiceUrl = invoiceUrl;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getIsArchived() {
        return isArchived;
    }
    
    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }
    
    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }
    
    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
    
    public UUID getArchivedBy() {
        return archivedBy;
    }
    
    public void setArchivedBy(UUID archivedBy) {
        this.archivedBy = archivedBy;
    }
    
    public String getArchiveReason() {
        return archiveReason;
    }
    
    public void setArchiveReason(String archiveReason) {
        this.archiveReason = archiveReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business Logic Methods with Security Considerations
    
    /**
     * Calculate total amount including fees, discounts, and taxes
     */
    private void calculateTotalAmount() {
        double total = this.amount != null ? this.amount : 0.0;
        
        // Add setup fee
        if (this.setupFee != null) {
            total += this.setupFee;
        }
        
        // Apply discount
        if (this.discountPercentage != null && this.discountPercentage > 0) {
            double discount = total * (this.discountPercentage / 100.0);
            total -= discount;
            this.discountAmount = discount;
        } else if (this.discountAmount != null && this.discountAmount > 0) {
            total -= this.discountAmount;
        }
        
        // Apply tax
        if (this.taxPercentage != null && this.taxPercentage > 0) {
            this.taxAmount = total * (this.taxPercentage / 100.0);
            total += this.taxAmount;
        } else if (this.taxAmount != null && this.taxAmount > 0) {
            total += this.taxAmount;
        }
        
        this.totalAmount = total;
    }
    
    /**
     * Calculate next billing date based on billing cycle
     */
    private LocalDateTime calculateNextBillingDate() {
        if (this.startDate == null) {
            return null;
        }
        
        LocalDateTime baseDate = this.lastBillingDate != null ? this.lastBillingDate : this.startDate;
        
        switch (this.billingCycle.toLowerCase()) {
            case "monthly":
                return baseDate.plusMonths(1);
            case "quarterly":
                return baseDate.plusMonths(3);
            case "yearly":
                return baseDate.plusYears(1);
            default:
                return baseDate.plusMonths(1);
        }
    }
    
    /**
     * Update status and related timestamps
     */
    private void updateStatusTimestamps(String newStatus) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (newStatus) {
            case "cancelled":
                if (this.cancellationDate == null) {
                    this.cancellationDate = now;
                }
                break;
            case "suspended":
                if (this.gracePeriodEnd == null) {
                    this.gracePeriodEnd = now.plusDays(this.gracePeriodDays);
                }
                break;
        }
    }
    
    /**
     * Update payment status and related timestamps
     */
    private void updatePaymentTimestamps(String newStatus) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (newStatus) {
            case "paid":
                this.lastPaymentDate = now;
                this.lastPaymentAmount = this.totalAmount;
                this.failedPaymentCount = 0;
                this.nextBillingDate = calculateNextBillingDate();
                break;
            case "failed":
                this.lastFailedPaymentDate = now;
                this.failedPaymentCount++;
                break;
        }
    }
    
    /**
     * Start trial period
     */
    public void startTrial(Integer trialDays) {
        this.trialDays = trialDays;
        this.trialStartDate = LocalDateTime.now();
        this.trialEndDate = this.trialStartDate.plusDays(trialDays);
        this.status = "trial";
    }
    
    /**
     * Check if subscription is in trial
     */
    public boolean isInTrial() {
        return "trial".equals(this.status) && 
               this.trialEndDate != null && 
               LocalDateTime.now().isBefore(this.trialEndDate);
    }
    
    /**
     * Check if trial has expired
     */
    public boolean isTrialExpired() {
        return this.trialEndDate != null && LocalDateTime.now().isAfter(this.trialEndDate);
    }
    
    /**
     * Activate subscription
     */
    public void activate() {
        this.status = "active";
        this.startDate = LocalDateTime.now();
        this.nextBillingDate = calculateNextBillingDate();
    }
    
    /**
     * Suspend subscription
     */
    public void suspend() {
        this.status = "suspended";
        this.gracePeriodEnd = LocalDateTime.now().plusDays(this.gracePeriodDays);
    }
    
    /**
     * Cancel subscription
     */
    public void cancel(UUID cancelledBy, String reason) {
        this.status = "cancelled";
        this.cancellationDate = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;
        this.autoRenew = false;
    }
    
    /**
     * Reactivate cancelled subscription
     */
    public void reactivate() {
        this.status = "active";
        this.cancellationDate = null;
        this.cancelledBy = null;
        this.cancellationReason = null;
        this.autoRenew = true;
        this.nextBillingDate = calculateNextBillingDate();
    }
    
    /**
     * Process payment
     */
    public void processPayment(Double amount, String paymentMethodId) {
        this.paymentStatus = "paid";
        this.paymentMethodId = paymentMethodId;
        this.lastPaymentDate = LocalDateTime.now();
        this.lastPaymentAmount = amount;
        this.failedPaymentCount = 0;
        this.nextBillingDate = calculateNextBillingDate();
    }
    
    /**
     * Process failed payment
     */
    public void processFailedPayment(String reason) {
        this.paymentStatus = "failed";
        this.lastFailedPaymentDate = LocalDateTime.now();
        this.lastFailedPaymentReason = reason;
        this.failedPaymentCount++;
        
        if (this.failedPaymentCount >= this.maxFailedPayments) {
            this.status = "suspended";
            this.gracePeriodEnd = LocalDateTime.now().plusDays(this.gracePeriodDays);
        }
    }
    
    /**
     * Process refund
     */
    public void processRefund(Double amount, String reason) {
        this.paymentStatus = "refunded";
        this.refundAmount = amount;
        this.refundDate = LocalDateTime.now();
        this.refundReason = reason;
    }
    
    /**
     * Check if subscription is active
     */
    public boolean isActive() {
        return "active".equals(this.status) || "trial".equals(this.status);
    }
    
    /**
     * Check if subscription is suspended
     */
    public boolean isSuspended() {
        return "suspended".equals(this.status);
    }
    
    /**
     * Check if subscription is cancelled
     */
    public boolean isCancelled() {
        return "cancelled".equals(this.status);
    }
    
    /**
     * Check if subscription is expired
     */
    public boolean isExpired() {
        return "expired".equals(this.status);
    }
    
    /**
     * Check if subscription needs renewal
     */
    public boolean needsRenewal() {
        return this.nextBillingDate != null && 
               LocalDateTime.now().isAfter(this.nextBillingDate) &&
               this.autoRenew;
    }
    
    /**
     * Check if subscription is in grace period
     */
    public boolean isInGracePeriod() {
        return this.gracePeriodEnd != null && 
               LocalDateTime.now().isBefore(this.gracePeriodEnd);
    }
    
    /**
     * Check if subscription can be cancelled
     */
    public boolean canBeCancelled() {
        return isActive() && !isCancelled();
    }
    
    /**
     * Get days until next billing
     */
    public long getDaysUntilNextBilling() {
        if (this.nextBillingDate == null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), this.nextBillingDate).toDays();
    }
    
    /**
     * Get days until trial expires
     */
    public long getDaysUntilTrialExpires() {
        if (this.trialEndDate == null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), this.trialEndDate).toDays();
    }
    
    /**
     * Get days until grace period expires
     */
    public long getDaysUntilGracePeriodExpires() {
        if (this.gracePeriodEnd == null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), this.gracePeriodEnd).toDays();
    }
    
    /**
     * Archive subscription with reason
     */
    public void archive(UUID archivedBy, String reason) {
        this.isArchived = true;
        this.archivedAt = LocalDateTime.now();
        this.archivedBy = archivedBy;
        this.archiveReason = reason;
    }
    
    /**
     * Unarchive subscription
     */
    public void unarchive() {
        this.isArchived = false;
        this.archivedAt = null;
        this.archivedBy = null;
        this.archiveReason = null;
    }
    
    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", planType=" + planType +
                ", planName='" + planName + '\'' +
                ", billingCycle='" + billingCycle + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", isArchived=" + isArchived +
                '}';
    }
}
