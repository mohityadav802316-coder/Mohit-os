-- MIGRATION 003: Headless CMS Sections, Deployments, and Change Requests

CREATE TABLE IF NOT EXISTS content_sections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    section_key VARCHAR(64) NOT NULL,
    payload JSONB NOT NULL,
    sync_status VARCHAR(64) NOT NULL DEFAULT 'LIVE_ON_WEBSITE',
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(project_id, section_key)
);

CREATE TABLE IF NOT EXISTS change_requests (
    id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    reason TEXT NOT NULL,
    request_type VARCHAR(64) NOT NULL,
    status VARCHAR(64) NOT NULL DEFAULT 'PENDING_APPROVAL',
    risk_level VARCHAR(32) NOT NULL DEFAULT 'LOW',
    target_environment VARCHAR(32) NOT NULL DEFAULT 'PRODUCTION',
    diff_summary TEXT NOT NULL,
    before_value TEXT,
    after_value TEXT,
    proposed_at BIGINT NOT NULL,
    approved_at BIGINT,
    approved_by VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS deployments (
    id VARCHAR(64) PRIMARY KEY,
    project_id VARCHAR(64) NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    commit_sha VARCHAR(64) NOT NULL,
    branch VARCHAR(128) NOT NULL DEFAULT 'main',
    status VARCHAR(64) NOT NULL DEFAULT 'QUEUED',
    environment VARCHAR(32) NOT NULL DEFAULT 'PRODUCTION',
    url VARCHAR(512),
    deployed_at BIGINT NOT NULL,
    approved_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
