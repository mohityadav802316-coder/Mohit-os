-- MIGRATION 002: Integrations, Encrypted Secrets Storage, and Audit Trail

CREATE TABLE IF NOT EXISTS integrations (
    id VARCHAR(64) PRIMARY KEY,
    service_key VARCHAR(64) UNIQUE NOT NULL,
    provider_name VARCHAR(128) NOT NULL,
    purpose TEXT NOT NULL,
    state VARCHAR(32) NOT NULL DEFAULT 'NOT_CONFIGURED',
    encrypted_credentials_payload TEXT,
    last_verified_at TIMESTAMPTZ,
    last_verification_latency_ms INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    actor VARCHAR(255) NOT NULL,
    action VARCHAR(128) NOT NULL,
    target VARCHAR(255) NOT NULL,
    project_id VARCHAR(64) REFERENCES projects(id) ON DELETE SET NULL,
    correlation_id VARCHAR(128) NOT NULL,
    metadata JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_created ON audit_logs (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_audit_correlation ON audit_logs (correlation_id);
