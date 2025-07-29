CREATE TABLE user_data (
                           user_id       UUID PRIMARY KEY,
                           first_name    VARCHAR(255) NOT NULL,
                           last_name     VARCHAR(255) NOT NULL,
                           patronymic    VARCHAR(255),
                           date_of_birth DATE NOT NULL,
                           avatar_key    VARCHAR(255),
                           CONSTRAINT fk_user_data_on_users FOREIGN KEY (user_id) REFERENCES users (id)
);