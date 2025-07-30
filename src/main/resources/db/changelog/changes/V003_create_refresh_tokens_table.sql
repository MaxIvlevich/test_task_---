CREATE TABLE refresh_tokens (
                                id          UUID PRIMARY KEY,
                                user_id     UUID NOT NULL,
                                token       VARCHAR(255) NOT NULL CONSTRAINT uk_refresh_tokens_token UNIQUE,
                                expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
                                CONSTRAINT fk_refresh_tokens_on_users FOREIGN KEY (user_id) REFERENCES users (id)
);