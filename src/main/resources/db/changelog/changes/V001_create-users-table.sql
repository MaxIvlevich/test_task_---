CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username      VARCHAR(255) CONSTRAINT uk_users_username UNIQUE,
    password      VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL CONSTRAINT uk_users_email UNIQUE,
    phone_number  VARCHAR(255) NOT NULL CONSTRAINT uk_users_phone_number UNIQUE
);

CREATE TABLE user_roles
(
    user_id UUID         NOT NULL,
    role    VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_roles_on_users FOREIGN KEY (user_id) REFERENCES users (id),
    PRIMARY KEY (user_id, role)
);
