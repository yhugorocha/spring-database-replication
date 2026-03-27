CREATE ROLE replicator WITH REPLICATION LOGIN PASSWORD 'replica123';
CREATE TABLE IF NOT EXISTS test_data (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO test_data (name) VALUES ('registro inicial');