-- V1__Create_initial_schema.sql
-- Initial database schema for Chatwoot multi-tenant system
-- This migration creates all core tables with proper constraints and indexes

-- Enable UUID extension for PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tenants table
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255) UNIQUE,
    custom_domain VARCHAR(255) UNIQUE,
    plan_type VARCHAR(50) NOT NULL DEFAULT 'FREE',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    max_users INTEGER NOT NULL DEFAULT 5,
    max_conversations INTEGER NOT NULL DEFAULT 100,
    max_storage_mb INTEGER NOT NULL DEFAULT 1000,
    features JSONB DEFAULT '{}',
    settings JSONB DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'AGENT',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    avatar_url VARCHAR(500),
    preferences JSONB DEFAULT '{}',
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    UNIQUE(tenant_id, email)
);

-- Create customers table
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    email VARCHAR(255),
    phone VARCHAR(50),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url VARCHAR(500),
    custom_attributes JSONB DEFAULT '{}',
    tags TEXT[],
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- Create conversations table
CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    assigned_agent_id UUID REFERENCES users(id),
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    subject VARCHAR(500),
    tags TEXT[],
    custom_attributes JSONB DEFAULT '{}',
    resolved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- Create messages table
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_type VARCHAR(20) NOT NULL CHECK (sender_type IN ('USER', 'CUSTOMER', 'SYSTEM', 'AI')),
    sender_id UUID,
    content TEXT NOT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT' CHECK (message_type IN ('TEXT', 'IMAGE', 'FILE', 'AUDIO', 'VIDEO', 'LOCATION')),
    metadata JSONB DEFAULT '{}',
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create subscriptions table
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    plan_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    current_period_start TIMESTAMP WITH TIME ZONE NOT NULL,
    current_period_end TIMESTAMP WITH TIME ZONE NOT NULL,
    cancel_at_period_end BOOLEAN DEFAULT FALSE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    stripe_subscription_id VARCHAR(255),
    paypal_subscription_id VARCHAR(255),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'GBP',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create automations table
CREATE TABLE automations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id UUID NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    trigger_type VARCHAR(50) NOT NULL CHECK (trigger_type IN ('MESSAGE', 'CONVERSATION', 'SCHEDULE', 'WEBHOOK')),
    trigger_conditions JSONB NOT NULL,
    actions JSONB NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX idx_users_tenant_id ON users(tenant_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);

CREATE INDEX idx_customers_tenant_id ON customers(tenant_id);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_status ON customers(status);

CREATE INDEX idx_conversations_tenant_id ON conversations(tenant_id);
CREATE INDEX idx_conversations_customer_id ON conversations(customer_id);
CREATE INDEX idx_conversations_assigned_agent_id ON conversations(assigned_agent_id);
CREATE INDEX idx_conversations_status ON conversations(status);
CREATE INDEX idx_conversations_created_at ON conversations(created_at);

CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_sender_type ON messages(sender_type);
CREATE INDEX idx_messages_created_at ON messages(created_at);

CREATE INDEX idx_subscriptions_tenant_id ON subscriptions(tenant_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscriptions_current_period_end ON subscriptions(current_period_end);

CREATE INDEX idx_automations_tenant_id ON automations(tenant_id);
CREATE INDEX idx_automations_trigger_type ON automations(trigger_type);
CREATE INDEX idx_automations_is_active ON automations(is_active);

-- Create unique constraints
CREATE UNIQUE INDEX idx_tenants_domain_unique ON tenants(domain) WHERE domain IS NOT NULL;
CREATE UNIQUE INDEX idx_tenants_custom_domain_unique ON tenants(custom_domain) WHERE custom_domain IS NOT NULL;

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_tenants_updated_at BEFORE UPDATE ON tenants
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_conversations_updated_at BEFORE UPDATE ON conversations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_messages_updated_at BEFORE UPDATE ON messages
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_subscriptions_updated_at BEFORE UPDATE ON subscriptions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_automations_updated_at BEFORE UPDATE ON automations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert default admin tenant
INSERT INTO tenants (id, name, domain, plan_type, status, max_users, max_conversations, max_storage_mb, features, settings)
VALUES (
    uuid_generate_v4(),
    'WeaveCode Admin',
    'admin.weavecode.co.uk',
    'ENTERPRISE',
    'ACTIVE',
    1000,
    100000,
    1000000,
    '{"ai_chatbot": true, "advanced_analytics": true, "custom_integrations": true, "priority_support": true}',
    '{"default_language": "en", "timezone": "Europe/London", "working_hours": {"start": "09:00", "end": "17:00"}}'
);

-- Insert default admin user (password: admin123)
INSERT INTO users (id, tenant_id, email, password_hash, first_name, last_name, role, status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'admin@weavecode.co.uk',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'System',
    'Administrator',
    'SUPER_ADMIN',
    'ACTIVE'
FROM tenants t 
WHERE t.domain = 'admin.weavecode.co.uk';

-- Create comments for documentation
COMMENT ON TABLE tenants IS 'Multi-tenant organisations using the Chatwoot system';
COMMENT ON TABLE users IS 'Users within each tenant (agents, admins, etc.)';
COMMENT ON TABLE customers IS 'End customers who interact with the system';
COMMENT ON TABLE conversations IS 'Customer support conversations';
COMMENT ON TABLE messages IS 'Individual messages within conversations';
COMMENT ON TABLE subscriptions IS 'Tenant subscription and billing information';
COMMENT ON TABLE automations IS 'AI-powered chatbots and workflow automation';
