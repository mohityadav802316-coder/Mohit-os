-- ============================================================
-- MASTER ADMIN — POSTGRESQL PRODUCTION RELATIONAL SCHEMA
-- Database: PostgreSQL 15+ / Neon / Supabase
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. USERS & ROLES
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'OWNER', -- OWNER, ADMIN, VIEWER
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Seed Owner
INSERT INTO users (email, full_name, role)
VALUES ('mohityadav802316@gmail.com', 'Mohit Yadav', 'OWNER')
ON CONFLICT (email) DO NOTHING;

-- 2. PROJECTS REGISTRY
CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    url VARCHAR(512) NOT NULL,
    icon VARCHAR(64) NOT NULL DEFAULT 'terminal',
    project_type VARCHAR(64) NOT NULL, -- PORTFOLIO, WEB_APP, BACKEND_API, etc.
    environment VARCHAR(64) NOT NULL DEFAULT 'PRODUCTION',
    health_status VARCHAR(64) NOT NULL DEFAULT 'HEALTHY',
    repository_url VARCHAR(512),
    hosting_provider VARCHAR(64) NOT NULL DEFAULT 'VERCEL',
    database_provider VARCHAR(64) NOT NULL DEFAULT 'POSTGRESQL',
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    last_sync_timestamp BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_projects_type ON projects (project_type);
CREATE INDEX IF NOT EXISTS idx_projects_archived ON projects (is_archived);

-- 3. CONTENT SECTIONS (WEBSITE CMS)
CREATE TABLE IF NOT EXISTS content_sections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    section_key VARCHAR(64) NOT NULL, -- 'hero', 'skills', 'projects', 'contact'
    payload JSONB NOT NULL,
    sync_status VARCHAR(64) NOT NULL DEFAULT 'LIVE_ON_WEBSITE',
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(project_id, section_key)
);

CREATE INDEX IF NOT EXISTS idx_content_project ON content_sections (project_id);

-- 4. CHANGE REQUESTS & APPROVALS
CREATE TABLE IF NOT EXISTS change_requests (
    id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    reason TEXT NOT NULL,
    request_type VARCHAR(64) NOT NULL, -- CONTENT_UPDATE, CODE_PATCH, CONFIG_UPDATE, DEPLOYMENT
    risk_level VARCHAR(64) NOT NULL DEFAULT 'LOW', -- LOW, MEDIUM, HIGH, CRITICAL
    creator_email VARCHAR(255) NOT NULL,
    target_environment VARCHAR(64) NOT NULL DEFAULT 'PRODUCTION',
    diff_summary TEXT,
    before_value TEXT,
    after_value TEXT,
    status VARCHAR(64) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED, EXECUTED
    reviewed_at TIMESTAMPTZ,
    reviewer_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cr_project_status ON change_requests (project_id, status);

-- 5. DEPLOYMENTS
CREATE TABLE IF NOT EXISTS deployments (
    id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    environment VARCHAR(64) NOT NULL DEFAULT 'PRODUCTION',
    status VARCHAR(64) NOT NULL, -- READY, BUILDING, QUEUED, ERROR
    commit_sha VARCHAR(64) NOT NULL,
    commit_message TEXT NOT NULL,
    branch VARCHAR(128) NOT NULL,
    deployment_url VARCHAR(512) NOT NULL,
    provider VARCHAR(64) NOT NULL DEFAULT 'VERCEL',
    triggered_by VARCHAR(255) NOT NULL,
    build_duration_seconds INT NOT NULL DEFAULT 0,
    logs JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_deployments_project ON deployments (project_id, created_at DESC);

-- 6. INTEGRATIONS METADATA (NO PLAINTEXT SECRETS)
CREATE TABLE IF NOT EXISTS integrations (
    id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    provider VARCHAR(64) NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    endpoint VARCHAR(512) NOT NULL,
    connection_status VARCHAR(64) NOT NULL DEFAULT 'CONNECTED',
    auth_method VARCHAR(64) NOT NULL DEFAULT 'BEARER_TOKEN',
    secret_key_ref VARCHAR(255), -- Reference to Google Secret Manager or Server Key Ring
    latency_ms INT NOT NULL DEFAULT 0,
    last_sync_timestamp BIGINT NOT NULL,
    last_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 7. MEDIA ASSETS
CREATE TABLE IF NOT EXISTS media_assets (
    id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(1024) NOT NULL,
    alt_text VARCHAR(512),
    category VARCHAR(64) NOT NULL DEFAULT 'General',
    is_reachable BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- 8. IMMUTABLE SECURITY AUDIT LOG
CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(64) PRIMARY KEY,
    actor VARCHAR(255) NOT NULL,
    action VARCHAR(128) NOT NULL,
    resource_type VARCHAR(64) NOT NULL,
    resource_id VARCHAR(128) NOT NULL,
    project_id VARCHAR(64),
    status VARCHAR(32) NOT NULL DEFAULT 'SUCCESS',
    correlation_id VARCHAR(64) NOT NULL,
    details TEXT,
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_audit_timestamp ON audit_logs (timestamp DESC);
CREATE INDEX IF NOT EXISTS idx_audit_correlation ON audit_logs (correlation_id);
