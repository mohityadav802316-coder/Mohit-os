-- MIGRATION 001: Initial Relational Tables & Enums
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'OWNER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO users (email, full_name, role)
VALUES ('mohityadav802316@gmail.com', 'Mohit Yadav', 'OWNER')
ON CONFLICT (email) DO NOTHING;

CREATE TABLE IF NOT EXISTS projects (
    id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    url VARCHAR(512) NOT NULL,
    icon VARCHAR(64) NOT NULL DEFAULT 'terminal',
    project_type VARCHAR(64) NOT NULL,
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
