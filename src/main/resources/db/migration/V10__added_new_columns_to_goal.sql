ALTER TABLE goals ADD COLUMN category VARCHAR(50);
ALTER TABLE goals ADD COLUMN type ENUM('PUBLIC', 'PRIVATE', 'FRIENDS_ONLY') NOT NULL DEFAULT 'PUBLIC';
ALTER TABLE goals ADD COLUMN private_code VARCHAR(20);
ALTER TABLE goals ADD COLUMN votes_to_mark_completed INT NOT NULL DEFAULT 2;
ADD CONSTRAINT chk_votes_to_mark_completed
CHECK (votes_to_mark_completed BETWEEN 2 AND 5);
ALTER TABLE goals CHANGE COLUMN goal_user goal_host BIGINT;
ALTER TABLE goals DROP FOREIGN KEY fk_goal_user;
ALTER TABLE goals
    ADD CONSTRAINT fk_goal_host FOREIGN KEY (goal_host) REFERENCES users(id);