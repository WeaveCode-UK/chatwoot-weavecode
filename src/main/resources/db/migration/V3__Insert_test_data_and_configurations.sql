-- V3__Insert_test_data_and_configurations.sql
-- Insert test data and initial configurations for development and testing

-- Insert sample tenants for testing different plan types
INSERT INTO tenants (id, name, domain, plan_type, status, max_users, max_conversations, max_storage_mb, features, settings, timezone, working_hours, sla_policies)
VALUES 
    (uuid_generate_v4(), 'TechStart Ltd', 'techstart.test', 'STARTER', 'ACTIVE', 10, 500, 5000, 
     '{"ai_chatbot": false, "advanced_analytics": false, "custom_integrations": false, "priority_support": false}',
     '{"default_language": "en", "timezone": "Europe/London", "working_hours": {"start": "09:00", "end": "17:00"}}',
     'Europe/London',
     '{"monday": {"start": "09:00", "end": "17:00"}, "tuesday": {"start": "09:00", "end": "17:00"}, "wednesday": {"start": "09:00", "end": "17:00"}, "thursday": {"start": "09:00", "end": "17:00"}, "friday": {"start": "09:00", "end": "17:00"}}',
     '{"response_time": 30, "resolution_time": 1440, "business_hours_only": true}'),
    
    (uuid_generate_v4(), 'Enterprise Corp', 'enterprise.test', 'PROFESSIONAL', 'ACTIVE', 50, 5000, 50000,
     '{"ai_chatbot": true, "advanced_analytics": true, "custom_integrations": false, "priority_support": false}',
     '{"default_language": "en", "timezone": "America/New_York", "working_hours": {"start": "08:00", "end": "18:00"}}',
     'America/New_York',
     '{"monday": {"start": "08:00", "end": "18:00"}, "tuesday": {"start": "08:00", "end": "18:00"}, "wednesday": {"start": "08:00", "end": "18:00"}, "thursday": {"start": "08:00", "end": "18:00"}, "friday": {"start": "08:00", "end": "18:00"}}',
     '{"response_time": 15, "resolution_time": 480, "business_hours_only": false}'),
    
    (uuid_generate_v4(), 'Global Solutions', 'globalsolutions.test', 'ENTERPRISE', 'ACTIVE', 200, 50000, 500000,
     '{"ai_chatbot": true, "advanced_analytics": true, "custom_integrations": true, "priority_support": true}',
     '{"default_language": "en", "timezone": "Asia/Tokyo", "working_hours": {"start": "09:00", "end": "18:00"}}',
     'Asia/Tokyo',
     '{"monday": {"start": "09:00", "end": "18:00"}, "tuesday": {"start": "09:00", "end": "18:00"}, "wednesday": {"start": "09:00", "end": "18:00"}, "thursday": {"start": "09:00", "end": "18:00"}, "friday": {"start": "09:00", "end": "18:00"}}',
     '{"response_time": 5, "resolution_time": 240, "business_hours_only": false}');

-- Insert sample users for each tenant
-- TechStart Ltd users
INSERT INTO users (id, tenant_id, email, password_hash, first_name, last_name, role, status, working_hours, skills, availability_status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'john.smith@techstart.test',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'John',
    'Smith',
    'TENANT_ADMIN',
    'ACTIVE',
    '{"monday": {"start": "09:00", "end": "17:00"}, "tuesday": {"start": "09:00", "end": "17:00"}, "wednesday": {"start": "09:00", "end": "17:00"}, "thursday": {"start": "09:00", "end": "17:00"}, "friday": {"start": "09:00", "end": "17:00"}}',
    ARRAY['customer_service', 'technical_support'],
    'ONLINE'
FROM tenants t 
WHERE t.domain = 'techstart.test';

INSERT INTO users (id, tenant_id, email, password_hash, first_name, last_name, role, status, working_hours, skills, availability_status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'sarah.jones@techstart.test',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'Sarah',
    'Jones',
    'AGENT',
    'ACTIVE',
    '{"monday": {"start": "09:00", "end": "17:00"}, "tuesday": {"start": "09:00", "end": "17:00"}, "wednesday": {"start": "09:00", "end": "17:00"}, "thursday": {"start": "09:00", "end": "17:00"}, "friday": {"start": "09:00", "end": "17:00"}}',
    ARRAY['customer_service'],
    'ONLINE'
FROM tenants t 
WHERE t.domain = 'techstart.test';

-- Enterprise Corp users
INSERT INTO users (id, tenant_id, email, password_hash, first_name, last_name, role, status, working_hours, skills, availability_status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'mike.wilson@enterprise.test',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'Mike',
    'Wilson',
    'TENANT_ADMIN',
    'ACTIVE',
    '{"monday": {"start": "08:00", "end": "18:00"}, "tuesday": {"start": "08:00", "end": "18:00"}, "wednesday": {"start": "08:00", "end": "18:00"}, "thursday": {"start": "08:00", "end": "18:00"}, "friday": {"start": "08:00", "end": "18:00"}}',
    ARRAY['customer_service', 'technical_support', 'sales_support'],
    'ONLINE'
FROM tenants t 
WHERE t.domain = 'enterprise.test';

INSERT INTO users (id, tenant_id, email, password_hash, first_name, last_name, role, status, working_hours, skills, availability_status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'emma.davis@enterprise.test',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'Emma',
    'Davis',
    'AGENT',
    'ACTIVE',
    '{"monday": {"start": "08:00", "end": "18:00"}, "tuesday": {"start": "08:00", "end": "18:00"}, "wednesday": {"start": "08:00", "end": "18:00"}, "thursday": {"start": "08:00", "end": "18:00"}, "friday": {"start": "08:00", "end": "18:00"}}',
    ARRAY['customer_service', 'technical_support'],
    'ONLINE'
FROM tenants t 
WHERE t.domain = 'enterprise.test';

-- Global Solutions users
INSERT INTO users (id, tenant_id, email, password_hash, first_name, last_name, role, status, working_hours, skills, availability_status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'david.lee@globalsolutions.test',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- admin123
    'David',
    'Lee',
    'TENANT_ADMIN',
    'ACTIVE',
    '{"monday": {"start": "09:00", "end": "18:00"}, "tuesday": {"start": "09:00", "end": "18:00"}, "wednesday": {"start": "09:00", "end": "18:00"}, "thursday": {"start": "09:00", "end": "18:00"}, "friday": {"start": "09:00", "end": "18:00"}}',
    ARRAY['customer_service', 'technical_support', 'sales_support', 'enterprise_support'],
    'ONLINE'
FROM tenants t 
WHERE t.domain = 'globalsolutions.test';

-- Insert sample customers for each tenant
-- TechStart Ltd customers
INSERT INTO customers (id, tenant_id, email, phone, first_name, last_name, custom_attributes, tags, status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'customer1@example.com',
    '+44 20 1234 5678',
    'Alice',
    'Johnson',
    '{"company": "Small Business Ltd", "industry": "Technology", "customer_since": "2024-01-15"}',
    ARRAY['new_customer', 'tech_savvy'],
    'ACTIVE'
FROM tenants t 
WHERE t.domain = 'techstart.test';

INSERT INTO customers (id, tenant_id, email, phone, first_name, last_name, custom_attributes, tags, status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'customer2@example.com',
    '+44 20 1234 5679',
    'Bob',
    'Brown',
    '{"company": "Startup Inc", "industry": "E-commerce", "customer_since": "2024-02-01"}',
    ARRAY['startup', 'ecommerce'],
    'ACTIVE'
FROM tenants t 
WHERE t.domain = 'techstart.test';

-- Enterprise Corp customers
INSERT INTO customers (id, tenant_id, email, phone, first_name, last_name, custom_attributes, tags, status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'enterprise1@example.com',
    '+1 555 123 4567',
    'Carol',
    'Wilson',
    '{"company": "Enterprise Solutions Inc", "industry": "Finance", "customer_since": "2023-06-01", "annual_revenue": "10000000"}',
    ARRAY['enterprise', 'finance', 'vip'],
    'ACTIVE'
FROM tenants t 
WHERE t.domain = 'enterprise.test';

-- Global Solutions customers
INSERT INTO customers (id, tenant_id, email, phone, first_name, last_name, custom_attributes, tags, status)
SELECT 
    uuid_generate_v4(),
    t.id,
    'global1@example.com',
    '+81 3 1234 5678',
    'Yuki',
    'Tanaka',
    '{"company": "Global Manufacturing Co", "industry": "Manufacturing", "customer_since": "2022-01-01", "annual_revenue": "50000000", "locations": ["Tokyo", "Osaka", "Nagoya"]}',
    ARRAY['global', 'manufacturing', 'enterprise', 'vip'],
    'ACTIVE'
FROM tenants t 
WHERE t.domain = 'globalsolutions.test';

-- Insert sample conversations
-- TechStart Ltd conversations
INSERT INTO conversations (id, tenant_id, customer_id, assigned_agent_id, status, priority, subject, tags, custom_attributes, priority_score, escalation_level)
SELECT 
    uuid_generate_v4(),
    t.id,
    c.id,
    u.id,
    'OPEN',
    'MEDIUM',
    'Product inquiry about pricing plans',
    ARRAY['pricing', 'inquiry'],
    '{"source": "website", "campaign": "spring_2024"}',
    75,
    0
FROM tenants t 
JOIN customers c ON c.tenant_id = t.id AND c.email = 'customer1@example.com'
JOIN users u ON u.tenant_id = t.id AND u.email = 'john.smith@techstart.test'
WHERE t.domain = 'techstart.test';

INSERT INTO conversations (id, tenant_id, customer_id, assigned_agent_id, status, priority, subject, tags, custom_attributes, priority_score, escalation_level)
SELECT 
    uuid_generate_v4(),
    t.id,
    c.id,
    u.id,
    'IN_PROGRESS',
    'HIGH',
    'Technical issue with login system',
    ARRAY['technical', 'urgent'],
    '{"source": "support_ticket", "priority": "high", "escalation_reason": "blocking_customer_access"}',
    125,
    1
FROM tenants t 
JOIN customers c ON c.tenant_id = t.id AND c.email = 'customer2@example.com'
JOIN users u ON u.tenant_id = t.id AND u.email = 'sarah.jones@techstart.test'
WHERE t.domain = 'techstart.test';

-- Enterprise Corp conversations
INSERT INTO conversations (id, tenant_id, customer_id, assigned_agent_id, status, priority, subject, tags, custom_attributes, priority_score, escalation_level)
SELECT 
    uuid_generate_v4(),
    t.id,
    c.id,
    u.id,
    'OPEN',
    'HIGH',
    'Enterprise integration requirements discussion',
    ARRAY['enterprise', 'integration', 'sales'],
    '{"source": "sales_team", "deal_size": "500000", "decision_maker": true}',
    100,
    0
FROM tenants t 
JOIN customers c ON c.tenant_id = t.id AND c.email = 'enterprise1@example.com'
JOIN users u ON u.tenant_id = t.id AND u.email = 'mike.wilson@enterprise.test'
WHERE t.domain = 'enterprise.test';

-- Insert sample messages for conversations
-- TechStart conversation 1 messages
INSERT INTO messages (id, conversation_id, sender_type, sender_id, content, message_type, is_read)
SELECT 
    uuid_generate_v4(),
    c.id,
    'CUSTOMER',
    NULL,
    'Hi, I am interested in your pricing plans. Can you tell me more about the different options available?',
    'TEXT',
    TRUE
FROM conversations c 
JOIN tenants t ON c.tenant_id = t.id
JOIN customers cust ON c.customer_id = cust.id
WHERE t.domain = 'techstart.test' AND cust.email = 'customer1@example.com' AND c.subject = 'Product inquiry about pricing plans';

INSERT INTO messages (id, conversation_id, sender_type, sender_id, content, message_type, is_read)
SELECT 
    uuid_generate_v4(),
    c.id,
    'USER',
    u.id,
    'Hello! Thank you for your interest in our pricing plans. We offer three main tiers: Starter (£29/month), Professional (£79/month), and Enterprise (custom pricing). Each plan includes different features and user limits. Would you like me to send you a detailed comparison?',
    'TEXT',
    FALSE
FROM conversations c 
JOIN tenants t ON c.tenant_id = t.id
JOIN users u ON c.assigned_agent_id = u.id
WHERE t.domain = 'techstart.test' AND u.email = 'john.smith@techstart.test' AND c.subject = 'Product inquiry about pricing plans';

-- TechStart conversation 2 messages
INSERT INTO messages (id, conversation_id, sender_type, sender_id, content, message_type, is_read)
SELECT 
    uuid_generate_v4(),
    c.id,
    'CUSTOMER',
    NULL,
    'I cannot log into my account. Getting an error message saying "Invalid credentials". I am sure my password is correct.',
    'TEXT',
    TRUE
FROM conversations c 
JOIN tenants t ON c.tenant_id = t.id
JOIN customers cust ON c.customer_id = cust.id
WHERE t.domain = 'techstart.test' AND cust.email = 'customer2@example.com' AND c.subject = 'Technical issue with login system';

INSERT INTO messages (id, conversation_id, sender_type, sender_id, content, message_type, is_read)
SELECT 
    uuid_generate_v4(),
    c.id,
    'USER',
    u.id,
    'I understand this is frustrating. Let me help you resolve this login issue. Can you please try resetting your password using the "Forgot Password" link? If that does not work, I can escalate this to our technical team for immediate assistance.',
    'TEXT',
    FALSE
FROM conversations c 
JOIN tenants t ON c.tenant_id = t.id
JOIN users u ON c.assigned_agent_id = u.id
WHERE t.domain = 'techstart.test' AND u.email = 'sarah.jones@techstart.test' AND c.subject = 'Technical issue with login system';

-- Insert sample tags for each tenant
-- TechStart Ltd tags
INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'pricing',
    '#3B82F6',
    'Questions about pricing and plans'
FROM tenants t 
WHERE t.domain = 'techstart.test';

INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'technical',
    '#EF4444',
    'Technical issues and bugs'
FROM tenants t 
WHERE t.domain = 'techstart.test';

INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'inquiry',
    '#10B981',
    'General product inquiries'
FROM tenants t 
WHERE t.domain = 'techstart.test';

-- Enterprise Corp tags
INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'enterprise',
    '#8B5CF6',
    'Enterprise-level inquiries and deals'
FROM tenants t 
WHERE t.domain = 'enterprise.test';

INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'integration',
    '#F59E0B',
    'Integration and API questions'
FROM tenants t 
WHERE t.domain = 'enterprise.test';

INSERT INTO tags (id, tenant_id, name, colour, description)
SELECT 
    uuid_generate_v4(),
    t.id,
    'sales',
    '#06B6D4',
    'Sales-related conversations'
FROM tenants t 
WHERE t.domain = 'enterprise.test';

-- Insert sample subscriptions
INSERT INTO subscriptions (id, tenant_id, plan_type, status, current_period_start, current_period_end, amount, currency)
SELECT 
    uuid_generate_v4(),
    t.id,
    'STARTER',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '1 month',
    CURRENT_DATE + INTERVAL '11 months',
    29.00,
    'GBP'
FROM tenants t 
WHERE t.domain = 'techstart.test';

INSERT INTO subscriptions (id, tenant_id, plan_type, status, current_period_start, current_period_end, amount, currency)
SELECT 
    uuid_generate_v4(),
    t.id,
    'PROFESSIONAL',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '2 months',
    CURRENT_DATE + INTERVAL '10 months',
    79.00,
    'USD'
FROM tenants t 
WHERE t.domain = 'enterprise.test';

INSERT INTO subscriptions (id, tenant_id, plan_type, status, current_period_start, current_period_end, amount, currency)
SELECT 
    uuid_generate_v4(),
    t.id,
    'ENTERPRISE',
    'ACTIVE',
    CURRENT_DATE - INTERVAL '6 months',
    CURRENT_DATE + INTERVAL '6 months',
    500.00,
    'USD'
FROM tenants t 
WHERE t.domain = 'globalsolutions.test';

-- Insert sample notification settings for users
INSERT INTO notification_settings (id, user_id, tenant_id, email_notifications, push_notifications, sms_notifications, notification_types, quiet_hours)
SELECT 
    uuid_generate_v4(),
    u.id,
    u.tenant_id,
    TRUE,
    TRUE,
    FALSE,
    '{"new_conversation": true, "message_received": true, "conversation_assigned": true, "conversation_resolved": true}',
    '{"enabled": true, "start": "22:00", "end": "08:00"}'
FROM users u 
WHERE u.role = 'TENANT_ADMIN';

INSERT INTO notification_settings (id, user_id, tenant_id, email_notifications, push_notifications, sms_notifications, notification_types, quiet_hours)
SELECT 
    uuid_generate_v4(),
    u.id,
    u.tenant_id,
    TRUE,
    TRUE,
    FALSE,
    '{"new_conversation": true, "message_received": true, "conversation_assigned": true}',
    '{"enabled": false, "start": "22:00", "end": "08:00"}'
FROM users u 
WHERE u.role = 'AGENT';

-- Insert sample performance metrics
INSERT INTO performance_metrics (id, tenant_id, user_id, metric_type, metric_value, metric_unit, period_start, period_end, metadata)
SELECT 
    uuid_generate_v4(),
    u.tenant_id,
    u.id,
    'conversations_resolved',
    15,
    'count',
    CURRENT_DATE - INTERVAL '30 days',
    CURRENT_DATE,
    '{"period": "monthly", "target": 20, "achievement_percentage": 75}'
FROM users u 
WHERE u.role = 'AGENT' AND u.email = 'sarah.jones@techstart.test';

INSERT INTO performance_metrics (id, tenant_id, user_id, metric_type, metric_value, metric_unit, period_start, period_end, metadata)
SELECT 
    uuid_generate_v4(),
    u.tenant_id,
    u.id,
    'average_response_time',
    8.5,
    'minutes',
    CURRENT_DATE - INTERVAL '30 days',
    CURRENT_DATE,
    '{"period": "monthly", "target": 15, "achievement_percentage": 100}'
FROM users u 
WHERE u.role = 'AGENT' AND u.email = 'sarah.jones@techstart.test';

-- Insert sample webhooks for Enterprise Corp
INSERT INTO webhooks (id, tenant_id, name, url, events, is_active, secret_key, headers)
SELECT 
    uuid_generate_v4(),
    t.id,
    'CRM Integration',
    'https://crm.enterprise.com/webhook/chatwoot',
    ARRAY['conversation.created', 'conversation.resolved', 'message.received'],
    TRUE,
    'webhook_secret_key_123',
    '{"Authorization": "Bearer crm_token_456", "Content-Type": "application/json"}'
FROM tenants t 
WHERE t.domain = 'enterprise.test';

-- Insert sample audit logs
INSERT INTO audit_logs (id, tenant_id, user_id, action, entity_type, entity_id, old_values, new_values, ip_address, user_agent)
SELECT 
    uuid_generate_v4(),
    u.tenant_id,
    u.id,
    'USER_LOGIN',
    'USER',
    u.id,
    NULL,
    '{"login_time": "' || CURRENT_TIMESTAMP || '", "ip_address": "192.168.1.100"}',
    '192.168.1.100',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
FROM users u 
WHERE u.email = 'john.smith@techstart.test';

INSERT INTO audit_logs (id, tenant_id, user_id, action, entity_type, entity_id, old_values, new_values, ip_address, user_agent)
SELECT 
    uuid_generate_v4(),
    u.tenant_id,
    u.id,
    'CONVERSATION_ASSIGNED',
    'CONVERSATION',
    c.id,
    '{"assigned_agent_id": null}',
    '{"assigned_agent_id": "' || u.id || '"}',
    '192.168.1.101',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
FROM users u 
JOIN conversations c ON c.tenant_id = u.tenant_id
WHERE u.email = 'sarah.jones@techstart.test' AND c.subject = 'Technical issue with login system';

-- Create comments for test data
COMMENT ON TABLE audit_logs IS 'Sample audit log entries for testing and development';
COMMENT ON TABLE performance_metrics IS 'Sample performance metrics for testing analytics';
COMMENT ON TABLE webhooks IS 'Sample webhook configurations for testing integrations';
