use goal_manager;
ALTER TABLE users
ADD COLUMN password VARCHAR(255) NOT NULL AFTER email;