CREATE TABLE goal_stars (
    goal_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (goal_id, user_id),
    CONSTRAINT fk_gs_goal FOREIGN KEY (goal_id) REFERENCES goals(goal_id) ON DELETE CASCADE,
    CONSTRAINT fk_gs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);