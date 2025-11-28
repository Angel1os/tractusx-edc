CREATE TABLE IF NOT EXISTS audit_record
(
    id               VARCHAR PRIMARY KEY,
    created_at       BIGINT      NOT NULL,
    event_type       VARCHAR     NOT NULL,
    event_id         VARCHAR,
    participant_id   VARCHAR,
    correlation_id   VARCHAR,
    event_data       JSONB,
    event_payload    TEXT,
    source           VARCHAR,
    event_timestamp  BIGINT      NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_audit_record_created_at ON audit_record (created_at);
CREATE INDEX IF NOT EXISTS idx_audit_record_event_type ON audit_record (event_type);
CREATE INDEX IF NOT EXISTS idx_audit_record_participant ON audit_record (participant_id);
CREATE INDEX IF NOT EXISTS idx_audit_record_correlation ON audit_record (correlation_id);

