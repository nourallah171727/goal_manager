use goal_manager;
CREATE TABLE follows (
    follower_id BIGINT NOT NULL,
    followee_id BIGINT NOT NULL,

    PRIMARY KEY (follower_id, followee_id),

    CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_followee FOREIGN KEY (followee_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT chk_no_self_follow CHECK (follower_id <> followee_id)
) ENGINE=InnoDB;