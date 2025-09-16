ALTER TABLE users
ADD CONSTRAINT uq_users_email UNIQUE (email),
ADD CONSTRAINT uq_users_username UNIQUE (username);