-- V2__Add_support_tables_and_improvements.sql
-- Additional support tables and schema improvements for Chatwoot system

-- Create audit_logs table for comprehensive activity tracking
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create file_attachments table for message attachments
CREATE TABLE file_attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    message_id UUID NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_hash VARCHAR(64),
    storage_provider VARCHAR(50) DEFAULT 'local',
    storage_metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id)
);

-- Create webhooks table for external integrations
CREATE TABLE webhooks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    events TEXT[] NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    secret_key VARCHAR(255),
    headers JSONB DEFAULT '{}',
    retry_count INTEGER DEFAULT 3,
    last_triggered_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- Create tags table for better tag management
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    colour VARCHAR(7) DEFAULT '#3B82F6',
    description TEXT,
    usage_count INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    UNIQUE(tenant_id, name)
);

-- Create conversation_tags junction table
CREATE TABLE conversation_tags (
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    PRIMARY KEY (conversation_id, tag_id)
);

-- Create customer_tags junction table
CREATE TABLE customer_tags (
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    PRIMARY KEY (customer_id, tag_id)
);

-- Create user_sessions table for session management
CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    session_token VARCHAR(255) NOT NULL,
    ip_address INET,
    user_agent TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create notification_settings table for user preferences
CREATE TABLE notification_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    email_notifications BOOLEAN DEFAULT TRUE,
    push_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    notification_types JSONB DEFAULT '{"new_conversation": true, "message_received": true, "conversation_assigned": true}',
    quiet_hours JSONB DEFAULT '{"enabled": false, "start": "22:00", "end": "08:00}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create performance_metrics table for analytics
CREATE TABLE performance_metrics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id),
    metric_type VARCHAR(50) NOT NULL,
    metric_value DECIMAL(10,2) NOT NULL,
    metric_unit VARCHAR(20),
    period_start TIMESTAMP WITH TIME ZONE NOT NULL,
    period_end TIMESTAMP WITH TIME ZONE NOT NULL,
    metadata JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Add new columns to existing tables for enhanced functionality
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS priority_score INTEGER DEFAULT 0;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS estimated_resolution_time INTEGER; -- in minutes
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS sla_deadline TIMESTAMP WITH TIME ZONE;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS escalation_level INTEGER DEFAULT 0;

ALTER TABLE users ADD COLUMN IF NOT EXISTS working_hours JSONB DEFAULT '{"monday": {"start": "09:00", "end": "17:00"}, "tuesday": {"start": "09:00", "end": "17:00"}, "wednesday": {"start": "09:00", "end": "17:00"}, "thursday": {"start": "09:00", "end": "17:00"}, "friday": {"start": "09:00", "end": "17:00"}}';
ALTER TABLE users ADD COLUMN IF NOT EXISTS skills TEXT[];
ALTER TABLE users ADD COLUMN IF NOT EXISTS availability_status VARCHAR(50) DEFAULT 'ONLINE';

ALTER TABLE tenants ADD COLUMN IF NOT EXISTS timezone VARCHAR(50) DEFAULT 'Europe/London';
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS working_hours JSONB DEFAULT '{"monday": {"start": "09:00", "end": "17:00"}, "tuesday": {"start": "09:00", "end": "17:00"}, "wednesday": {"start": "09:00", "end": "17:00"}, "thursday": {"start": "09:00", "end": "17:00"}, "friday": {"start": "09:00", "end": "17:00"}}';
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS sla_policies JSONB DEFAULT '{"response_time": 15, "resolution_time": 480, "business_hours_only": true}';

-- Create indexes for new tables
CREATE INDEX idx_audit_logs_tenant_id ON audit_logs(tenant_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity_type ON audit_logs(entity_type);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

CREATE INDEX idx_file_attachments_message_id ON file_attachments(message_id);
CREATE INDEX idx_file_attachments_tenant_id ON file_attachments(tenant_id);
CREATE INDEX idx_file_attachments_created_at ON file_attachments(created_at);

CREATE INDEX idx_webhooks_tenant_id ON webhooks(tenant_id);
CREATE INDEX idx_webhooks_is_active ON webhooks(is_active);

CREATE INDEX idx_tags_tenant_id ON tags(tenant_id);
CREATE INDEX idx_tags_name ON tags(name);

CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_session_token ON user_sessions(session_token);
CREATE INDEX idx_user_sessions_expires_at ON user_sessions(expires_at);

CREATE INDEX idx_notification_settings_user_id ON notification_settings(user_id);
CREATE INDEX idx_notification_settings_tenant_id ON notification_settings(tenant_id);

CREATE INDEX idx_performance_metrics_tenant_id ON performance_metrics(tenant_id);
CREATE INDEX idx_performance_metrics_user_id ON performance_metrics(user_id);
CREATE INDEX idx_performance_metrics_metric_type ON performance_metrics(metric_type);
CREATE INDEX idx_performance_metrics_period_start ON performance_metrics(period_start);

-- Create indexes for new columns in existing tables
CREATE INDEX idx_conversations_priority_score ON conversations(priority_score);
CREATE INDEX idx_conversations_sla_deadline ON conversations(sla_deadline);
CREATE INDEX idx_conversations_escalation_level ON conversations(escalation_level);

CREATE INDEX idx_users_availability_status ON users(availability_status);

-- Create triggers for new tables
CREATE TRIGGER update_webhooks_updated_at BEFORE UPDATE ON webhooks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notification_settings_updated_at BEFORE UPDATE ON notification_settings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create function to calculate conversation priority score
CREATE OR REPLACE FUNCTION calculate_conversation_priority()
RETURNS TRIGGER AS $$
BEGIN
    -- Priority score based on various factors
    NEW.priority_score = 0;
    
    -- Base score for priority level
    CASE NEW.priority
        WHEN 'HIGH' THEN NEW.priority_score := NEW.priority_score + 100;
        WHEN 'MEDIUM' THEN NEW.priority_score := NEW.priority_score + 50;
        WHEN 'LOW' THEN NEW.priority_score := NEW.priority_score + 25;
    END CASE;
    
    -- Add score for escalation level
    NEW.priority_score := NEW.priority_score + (NEW.escalation_level * 25);
    
    -- Add score for age (older conversations get higher priority)
    NEW.priority_score := NEW.priority_score + EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - NEW.created_at)) / 3600;
    
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for priority calculation
CREATE TRIGGER calculate_conversation_priority_trigger
    BEFORE INSERT OR UPDATE ON conversations
    FOR EACH ROW EXECUTE FUNCTION calculate_conversation_priority();

-- Insert default tags for the admin tenant
INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'urgent',
    '#EF4444',
    'High priority conversations that require immediate attention'
FROM tenants t 
WHERE t.domain = 'admin.weavecode.co.uk';

INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'bug',
    '#F59E0B',
    'Technical issues and bug reports'
FROM tenants t 
WHERE t.domain = 'admin.weavecode.co.uk';

INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'feature-request',
    '#10B981',
    'New feature requests from customers'
FROM tenants t 
WHERE t.domain = 'admin.weavecode.co.uk';

-- Create comments for new tables
COMMENT ON TABLE audit_logs IS 'Comprehensive audit trail for all system activities';
COMMENT ON TABLE file_attachments IS 'File attachments for messages (images, documents, etc.)';
COMMENT ON TABLE webhooks IS 'External webhook configurations for integrations';
COMMENT ON TABLE tags IS 'Customisable tags for conversations and customers';
COMMENT ON TABLE conversation_tags IS 'Many-to-many relationship between conversations and tags';
COMMENT ON TABLE customer_tags IS 'Many-to-many relationship between customers and tags';
COMMENT ON TABLE user_sessions IS 'User session management for security and analytics';
COMMENT ON TABLE notification_settings IS 'User preferences for notifications';
COMMENT ON TABLE performance_metrics IS 'Performance tracking and analytics data';
