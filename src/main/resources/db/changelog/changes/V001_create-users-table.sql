CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
                       id             UUID PRIMARY KEY    DEFAULT uuid_generate_v4(),
                       password      VARCHAR(255) NOT NULL,
                       last_name     VARCHAR(255) NOT NULL,
                       first_name    VARCHAR(255) NOT NULL,
                       patronymic    VARCHAR(255),
                       username      VARCHAR(255) CONSTRAINT uk_users_username UNIQUE,
                       date_of_birth DATE NOT NULL,
                       email         VARCHAR(255) NOT NULL CONSTRAINT uk_users_email UNIQUE,
                       phone_number  VARCHAR(255) NOT NULL CONSTRAINT uk_users_phone_number UNIQUE,
                       avatar_key    VARCHAR(255)
);

CREATE TABLE user_roles (
                            user_id UUID         NOT NULL,
                            role    VARCHAR(255) NOT NULL,
                            CONSTRAINT fk_user_roles_on_users FOREIGN KEY (user_id) REFERENCES users (id),
                            PRIMARY KEY (user_id, role)
);