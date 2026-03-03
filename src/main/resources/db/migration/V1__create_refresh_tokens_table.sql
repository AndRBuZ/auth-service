CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id serial PRIMARY KEY,
    user_id INT NOT NULL,
    token_hash text NOT NULL,
    created_at timestamp NOT NULL DEFAULT now(),
    expired_at timestamp NOT NULL,
    revoked boolean DEFAULT false
);

CREATE INDEX idx_refresh_tokens_lookup
ON refresh_tokens (user_id, token_hash);
