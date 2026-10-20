CREATE EXTENSION IF NOT EXISTS vector;

ALTER TABLE transaction ADD COLUMN embedding vector(768);

CREATE INDEX idx_transaction_embedding ON transaction
    USING hnsw (embedding vector_cosine_ops);