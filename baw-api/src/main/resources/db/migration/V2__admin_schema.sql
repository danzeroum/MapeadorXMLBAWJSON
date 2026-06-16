-- Process Veritas V2: sources and score_methodology tables

CREATE TABLE sources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('TWX_FILE','BAW_SERVER')),
    host TEXT,
    credentials JSONB,
    status TEXT NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','INACTIVE')),
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE score_methodology (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    version TEXT NOT NULL,
    weights JSONB NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO score_methodology (version, weights, active)
VALUES ('1.0', '{"complexity":0.3,"standardization":0.25,"automationReadiness":0.3,"maintainability":0.15}', TRUE);
