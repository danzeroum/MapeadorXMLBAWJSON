-- Process Veritas initial schema
-- V1: users, runs, reports, audit_log, platform_settings, usage_records

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('ADMIN','ANALISTA','AUDITOR','VISUALIZADOR')),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    password_hash TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    process_name TEXT NOT NULL,
    process_id TEXT NOT NULL,
    twx_filename TEXT,
    status TEXT NOT NULL CHECK (status IN ('QUEUED','RUNNING','DONE','FAILED')),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    duration_ms BIGINT,
    error_message TEXT,
    author_id UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX runs_status_idx ON runs (status);
CREATE INDEX runs_author_idx ON runs (author_id);

CREATE TABLE reports (
    run_id UUID PRIMARY KEY REFERENCES runs(id) ON DELETE CASCADE,
    payload JSONB NOT NULL,
    schema_ver TEXT NOT NULL DEFAULT '2.0',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX reports_payload_gin ON reports USING GIN (payload);

CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    actor_id UUID REFERENCES users(id),
    actor_email TEXT NOT NULL,
    action TEXT NOT NULL,
    resource TEXT NOT NULL,
    detail JSONB,
    ip_address INET,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE platform_settings (
    key TEXT PRIMARY KEY,
    value JSONB NOT NULL,
    updated_by UUID REFERENCES users(id),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE usage_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id),
    period DATE NOT NULL,
    run_count INTEGER NOT NULL DEFAULT 0,
    storage_mb NUMERIC(10,2),
    UNIQUE(user_id, period)
);

INSERT INTO users (email, name, role)
VALUES ('admin@processveritas.local', 'Admin', 'ADMIN');
