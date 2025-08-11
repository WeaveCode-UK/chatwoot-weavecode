package com.weavecode.chatwoot.repository;

import com.weavecode.chatwoot.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Message entity
 * Provides data access methods for message management
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Repository
public interface MessageRepository extends BaseRepository<Message, UUID> {

    /**
     * Find messages by conversation ID
     */
    List<Message> findByConversationId(UUID conversationId);

    /**
     * Find messages by sender ID
     */
    List<Message> findBySenderId(UUID senderId);

    /**
     * Find messages by sender type
     */
    List<Message> findBySenderType(String senderType);

    /**
     * Find messages by conversation ID and sender type
     */
    List<Message> findByConversationIdAndSenderType(UUID conversationId, String senderType);

    /**
     * Find messages by content type
     */
    List<Message> findByContentType(String contentType);

    /**
     * Find messages by message type
     */
    List<Message> findByMessageType(String messageType);

    /**
     * Find messages by delivery status
     */
    List<Message> findByDeliveryStatus(String deliveryStatus);

    /**
     * Find messages by priority
     */
    List<Message> findByPriority(String priority);

    /**
     * Find messages by tag
     */
    @Query("SELECT m FROM Message m WHERE m.tags LIKE %:tag%")
    List<Message> findByTag(@Param("tag") String tag);

    /**
     * Find messages by sentiment score range
     */
    @Query("SELECT m FROM Message m WHERE m.sentimentScore BETWEEN :minScore AND :maxScore")
    List<Message> findBySentimentScoreBetween(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);

    /**
     * Find messages by intent
     */
    List<Message> findByIntent(String intent);

    /**
     * Find messages by confidence score range
     */
    @Query("SELECT m FROM Message m WHERE m.confidenceScore BETWEEN :minScore AND :maxScore")
    List<Message> findByConfidenceScoreBetween(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);

    /**
     * Find messages created after specific date
     */
    List<Message> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find messages created between dates
     */
    List<Message> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find messages by delivery date
     */
    List<Message> findByDeliveredAtAfter(LocalDateTime date);

    /**
     * Find messages by read date
     */
    List<Message> findByReadAtAfter(LocalDateTime date);

    /**
     * Find messages by edit date
     */
    List<Message> findByEditedAtAfter(LocalDateTime date);

    /**
     * Find messages by delete date
     */
    List<Message> findByDeletedAtAfter(LocalDateTime date);

    /**
     * Find messages by parent message ID
     */
    List<Message> findByParentMessageId(UUID parentMessageId);

    /**
     * Find messages by thread ID
     */
    List<Message> findByThreadId(UUID threadId);

    /**
     * Find messages by read by user
     */
    List<Message> findByReadBy(UUID readBy);

    /**
     * Find messages by edited by user
     */
    List<Message> findByEditedBy(UUID editedBy);

    /**
     * Find messages by deleted by user
     */
    List<Message> findByDeletedBy(UUID deletedBy);

    /**
     * Find messages by failed reason
     */
    @Query("SELECT m FROM Message m WHERE m.failedReason LIKE %:reason%")
    List<Message> findByFailedReason(@Param("reason") String reason);

    /**
     * Find messages by edit reason
     */
    @Query("SELECT m FROM Message m WHERE m.editReason LIKE %:reason%")
    List<Message> findByEditReason(@Param("reason") String reason);

    /**
     * Find messages by delete reason
     */
    @Query("SELECT m FROM Message m WHERE m.deleteReason LIKE %:reason%")
    List<Message> findByDeleteReason(@Param("reason") String reason);

    /**
     * Find messages by custom field value
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:value%")
    List<Message> findByCustomFieldValue(@Param("value") String value);

    /**
     * Find messages by attachment type
     */
    @Query("SELECT m FROM Message m WHERE m.attachments::text LIKE %:attachmentType%")
    List<Message> findByAttachmentType(@Param("attachmentType") String attachmentType);

    /**
     * Count messages by conversation ID
     */
    long countByConversationId(UUID conversationId);

    /**
     * Count messages by sender ID
     */
    long countBySenderId(UUID senderId);

    /**
     * Count messages by sender type
     */
    long countBySenderType(String senderType);

    /**
     * Count messages by content type
     */
    long countByContentType(String contentType);

    /**
     * Count messages by message type
     */
    long countByMessageType(String messageType);

    /**
     * Count messages by delivery status
     */
    long countByDeliveryStatus(String deliveryStatus);

    /**
     * Count messages by priority
     */
    long countByPriority(String priority);

    /**
     * Count messages by intent
     */
    long countByIntent(String intent);

    /**
     * Count messages created in specific month
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE YEAR(m.createdAt) = :year AND MONTH(m.createdAt) = :month")
    long countByCreatedYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Count messages by conversation ID and sender type
     */
    long countByConversationIdAndSenderType(UUID conversationId, String senderType);

    /**
     * Find messages with pagination
     */
    Page<Message> findAll(Pageable pageable);

    /**
     * Find messages by conversation ID with pagination
     */
    Page<Message> findByConversationId(UUID conversationId, Pageable pageable);

    /**
     * Find messages by sender ID with pagination
     */
    Page<Message> findBySenderId(UUID senderId, Pageable pageable);

    /**
     * Find messages by sender type with pagination
     */
    Page<Message> findBySenderType(String senderType, Pageable pageable);

    /**
     * Find messages by content type with pagination
     */
    Page<Message> findByContentType(String contentType, Pageable pageable);

    /**
     * Find messages by message type with pagination
     */
    Page<Message> findByMessageType(String messageType, Pageable pageable);

    /**
     * Find messages by delivery status with pagination
     */
    Page<Message> findByDeliveryStatus(String deliveryStatus, Pageable pageable);

    /**
     * Find messages by priority with pagination
     */
    Page<Message> findByPriority(String priority, Pageable pageable);

    /**
     * Find messages by intent with pagination
     */
    Page<Message> findByIntent(String intent, Pageable pageable);

    /**
     * Search messages by content containing text
     */
    Page<Message> findByContentContainingIgnoreCase(String content, Pageable pageable);

    /**
     * Find messages by multiple criteria
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(:conversationId IS NULL OR m.conversationId = :conversationId) AND " +
           "(:senderType IS NULL OR m.senderType = :senderType) AND " +
           "(:contentType IS NULL OR m.contentType = :contentType) AND " +
           "(:messageType IS NULL OR m.messageType = :messageType)")
    Page<Message> findByMultipleCriteria(
            @Param("conversationId") UUID conversationId,
            @Param("senderType") String senderType,
            @Param("contentType") String contentType,
            @Param("messageType") String messageType,
            Pageable pageable
    );

    /**
     * Find messages with failed delivery
     */
    @Query("SELECT m FROM Message m WHERE m.deliveryStatus = 'failed'")
    List<Message> findMessagesWithFailedDelivery();

    /**
     * Find messages that can be retried
     */
    @Query("SELECT m FROM Message m WHERE m.deliveryStatus = 'failed' AND m.retryCount < m.maxRetries")
    List<Message> findMessagesThatCanBeRetried();

    /**
     * Find messages by retry count
     */
    @Query("SELECT m FROM Message m WHERE m.retryCount >= :minRetryCount")
    List<Message> findByMinRetryCount(@Param("minRetryCount") Integer minRetryCount);

    /**
     * Find messages by max retries
     */
    @Query("SELECT m FROM Message m WHERE m.maxRetries = :maxRetries")
    List<Message> findByMaxRetries(@Param("maxRetries") Integer maxRetries);

    /**
     * Find messages by sentiment score
     */
    @Query("SELECT m FROM Message m WHERE m.sentimentScore >= :minScore")
    List<Message> findByMinSentimentScore(@Param("minScore") Double minScore);

    /**
     * Find messages by confidence score
     */
    @Query("SELECT m FROM Message m WHERE m.confidenceScore >= :minScore")
    List<Message> findByMinConfidenceScore(@Param("minScore") Double minScore);

    /**
     * Find messages by age in minutes
     */
    @Query("SELECT m FROM Message m WHERE m.createdAt < :cutoffTime")
    List<Message> findMessagesOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find messages by custom field
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:fieldName%")
    List<Message> findByCustomField(@Param("fieldName") String fieldName);

    /**
     * Find messages by audit trail
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:auditEvent%")
    List<Message> findByAuditEvent(@Param("auditEvent") String auditEvent);

    /**
     * Find messages by language
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:language%")
    List<Message> findByLanguage(@Param("language") String language);

    /**
     * Find messages by country
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:country%")
    List<Message> findByCountry(@Param("country") String country);

    /**
     * Find messages by timezone
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:timezone%")
    List<Message> findByTimezone(@Param("timezone") String timezone);

    /**
     * Find messages by platform
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:platform%")
    List<Message> findByPlatform(@Param("platform") String platform);

    /**
     * Find messages by browser
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:browser%")
    List<Message> findByBrowser(@Param("browser") String browser);

    /**
     * Find messages by operating system
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:operatingSystem%")
    List<Message> findByOperatingSystem(@Param("operatingSystem") String operatingSystem);

    /**
     * Find messages by device type
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:deviceType%")
    List<Message> findByDeviceType(@Param("deviceType") String deviceType);

    /**
     * Find messages by user agent
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:userAgent%")
    List<Message> findByUserAgent(@Param("userAgent") String userAgent);

    /**
     * Find messages by IP address
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:ipAddress%")
    List<Message> findByIpAddress(@Param("ipAddress") String ipAddress);

    /**
     * Find messages by location
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:location%")
    List<Message> findByLocation(@Param("location") String location);

    /**
     * Find messages by currency
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:currency%")
    List<Message> findByCurrency(@Param("currency") String currency);

    /**
     * Find messages by region
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:region%")
    List<Message> findByRegion(@Param("region") String region);

    /**
     * Find messages by city
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:city%")
    List<Message> findByCity(@Param("city") String city);

    /**
     * Find messages by postal code
     */
    @Query("SELECT m FROM Message m WHERE m.metadata::text LIKE %:postalCode%")
    List<Message> findByPostalCode(@Param("postalCode") String postalCode);
}
