# Chatwoot Multi-Tenant System - Entities Implemented

## Overview
This document provides a comprehensive overview of all the core entities that have been implemented for the Chatwoot multi-tenant backend system. All entities follow enterprise-grade security patterns, implement comprehensive audit trails, and use British English spelling conventions.

## Core Entities

### 1. UserRole (Enum)
**Purpose**: Defines user roles and permissions across the multi-tenant system
**Key Features**:
- Role-based access control (RBAC)
- Permission checking methods
- Display names for UI presentation
- Hierarchical role structure

**Values**:
- `SUPER_ADMIN`: Full system access
- `TENANT_ADMIN`: Tenant-level administration
- `AGENT`: Customer support agent
- `MANAGER`: Team management
- `ANALYST`: Data analysis and reporting
- `VIEWER`: Read-only access

### 2. PlanType (Enum)
**Purpose**: Defines subscription plan types with features and pricing
**Key Features**:
- Plan categorisation (Basic, Professional, Enterprise)
- Feature sets per plan
- Pricing information
- Plan comparison methods

**Values**:
- `BASIC`: Entry-level plan
- `PROFESSIONAL`: Mid-tier plan
- `ENTERPRISE`: High-end plan
- `CUSTOM`: Customised plan

### 3. ConversationStatus (Enum)
**Purpose**: Manages conversation lifecycle and state transitions
**Key Features**:
- Status validation
- State transition rules
- Business logic methods
- SLA compliance checking

**Values**:
- `OPEN`: New conversation
- `IN_PROGRESS`: Active conversation
- `WAITING`: Waiting for response
- `RESOLVED`: Successfully resolved
- `ESCALATED`: Escalated to higher level
- `CLOSED`: Conversation closed

### 4. Tenant
**Purpose**: Represents client companies in the multi-tenant system
**Key Features**:
- Multi-tenant isolation
- Plan management
- Branding customisation
- Contact information
- Usage limits and quotas
- Audit trails

**Key Fields**:
- `id`: Unique identifier
- `companyName`: Company name
- `planType`: Subscription plan
- `domain`: Custom domain
- `branding`: Logo, colours, customisation
- `limits`: Usage limits and quotas
- `contactInfo`: Primary contact details
- `settings`: Tenant-specific configurations

### 5. User
**Purpose**: Represents user accounts across all tenants
**Key Features**:
- Multi-tenant user management
- Role-based permissions
- Authentication details
- Activity tracking
- Security features

**Key Fields**:
- `id`: Unique identifier
- `tenantId`: Associated tenant
- `email`: User email address
- `role`: User role and permissions
- `status`: Account status
- `lastLoginAt`: Last login timestamp
- `twoFactorEnabled`: 2FA status
- `preferences`: User preferences

### 6. Conversation
**Purpose**: Manages chat conversations between customers and agents
**Key Features**:
- Multi-channel support (web, WhatsApp, Instagram, Facebook, Telegram)
- SLA management
- Priority handling
- Assignment and escalation
- Customer satisfaction tracking
- Archiving and audit trails

**Key Fields**:
- `id`: Unique identifier
- `title`: Conversation title
- `status`: Current status
- `priority`: Priority level
- `assignedTo`: Assigned agent
- `customerId`: Customer reference
- `source`: Channel source
- `slaTarget`: SLA target time
- `tags`: Categorisation tags
- `metadata`: Additional data

**Business Logic Methods**:
- Status transitions with audit trails
- SLA breach detection
- Escalation handling
- Archive/unarchive operations
- Tag management

### 7. Message
**Purpose**: Individual messages within conversations
**Key Features**:
- Multi-format content (text, image, file, audio, video)
- Delivery status tracking
- Edit and delete with audit trails
- AI integration (sentiment analysis, intent detection)
- Threading support
- Attachment handling

**Key Fields**:
- `id`: Unique identifier
- `conversationId`: Parent conversation
- `senderId`: Message sender
- `senderType`: Sender type (user, agent, bot, system)
- `content`: Message content
- `contentType`: Content format
- `attachments`: File attachments
- `deliveryStatus`: Delivery tracking
- `sentimentScore`: AI sentiment analysis
- `intent`: AI intent detection

**Business Logic Methods**:
- Delivery status updates
- Message editing with audit trails
- Delete/restore operations
- Tag management
- AI analysis integration

### 8. Customer
**Purpose**: End users across all tenants
**Key Features**:
- Multi-tenant customer management
- GDPR/LGPD compliance
- Contact information
- Interaction history
- Customer satisfaction tracking
- Blocking and archiving

**Key Fields**:
- `id`: Unique identifier
- `tenantId`: Associated tenant
- `name`: Customer name
- `email`: Email address
- `phoneNumber`: Phone number
- `companyName`: Company name
- `gdprConsent`: GDPR consent status
- `marketingConsent`: Marketing consent
- `totalConversations`: Interaction count
- `customerSatisfactionScore`: Satisfaction rating

**Business Logic Methods**:
- GDPR consent management
- Contact history tracking
- Satisfaction score updates
- Block/unblock operations
- Archive/unarchive operations

### 9. Subscription
**Purpose**: Manages tenant subscription plans and billing
**Key Features**:
- Multi-tier pricing plans
- Trial periods
- Billing cycles (monthly, quarterly, yearly)
- Payment processing
- Grace periods and suspensions
- Refund handling

**Key Fields**:
- `id`: Unique identifier
- `tenantId`: Associated tenant
- `planType`: Plan type
- `billingCycle`: Billing frequency
- `amount`: Base amount
- `currency`: Currency (default: GBP)
- `trialDays`: Trial period
- `autoRenew`: Auto-renewal setting
- `paymentStatus`: Payment status
- `nextBillingDate`: Next billing date

**Business Logic Methods**:
- Total amount calculation (fees, discounts, taxes)
- Trial management
- Payment processing
- Subscription lifecycle management
- Rollback capabilities

### 10. Automation
**Purpose**: AI-powered chatbots and workflow automation
**Key Features**:
- OpenAI GPT integration
- Multi-language support
- Sentiment analysis
- Intent detection
- Entity extraction
- Business hours management
- Testing and deployment workflows

**Key Fields**:
- `id`: Unique identifier
- `tenantId`: Associated tenant
- `name`: Automation name
- `type`: Type (chatbot, workflow, trigger, scheduled)
- `aiModel`: AI model (GPT-4, GPT-3.5, Claude)
- `aiTemperature`: Response creativity
- `triggers`: Automation triggers
- `conditions`: Execution conditions
- `actions`: Automation actions
- `escalationThreshold`: Escalation rules

**Business Logic Methods**:
- AI configuration management
- Workflow deployment
- Testing and approval workflows
- Rollback capabilities
- Performance monitoring

## Database Schema Design

### Multi-Tenant Architecture
- **Shared Schema**: Contains tenant and user information
- **Tenant-Specific Schemas**: Isolated data per tenant
- **Hybrid Approach**: Shared schemas initially, separate databases for growth

### Security Features
- UUID primary keys for security
- Comprehensive audit trails
- GDPR/LGPD compliance
- Role-based access control
- Data encryption support

### Performance Optimisations
- Indexed foreign keys
- JSONB fields for flexible data
- Efficient query patterns
- Connection pooling ready

## Next Steps

### Immediate Priorities
1. **Repository Layer**: Implement JPA repositories for all entities
2. **Service Layer**: Business logic services with transaction management
3. **Controller Layer**: REST API endpoints
4. **Security Configuration**: JWT authentication and authorization

### Medium Term
1. **Database Migrations**: Flyway migration scripts
2. **API Documentation**: OpenAPI/Swagger documentation
3. **Testing**: Unit and integration tests
4. **Monitoring**: Health checks and metrics

### Long Term
1. **Performance Optimisation**: Query optimisation and caching
2. **Scalability**: Horizontal scaling and load balancing
3. **Advanced Features**: Real-time notifications, WebSocket support
4. **Analytics**: Advanced reporting and insights

## Technical Standards

### Code Quality
- **British English**: All code and documentation uses British spelling
- **Java 17**: Modern Java features and syntax
- **Spring Boot 3.2**: Latest stable version
- **JPA/Hibernate**: Object-relational mapping
- **Validation**: Bean Validation with custom messages

### Security Standards
- **Input Validation**: Comprehensive validation on all inputs
- **SQL Injection Prevention**: Parameterized queries via JPA
- **XSS Protection**: Output encoding and sanitisation
- **CSRF Protection**: Token-based protection
- **Rate Limiting**: API rate limiting and throttling

### Performance Standards
- **Response Times**: < 200ms for standard operations
- **Database Queries**: Optimised with proper indexing
- **Caching**: Redis integration for performance
- **Connection Pooling**: Efficient database connections
- **Async Processing**: Background job processing

## Conclusion

The implemented entities provide a solid foundation for a comprehensive, enterprise-grade multi-tenant chat system. The architecture supports:

- **Scalability**: Multi-tenant design with growth potential
- **Security**: Comprehensive security patterns and compliance
- **Flexibility**: Configurable features and customisation
- **Performance**: Optimised database design and caching
- **Maintainability**: Clean code structure and documentation

All entities are ready for the next development phase, which includes implementing the repository, service, and controller layers to create a fully functional API.
