CREATE TABLE uploads (
    user_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    current_votes INT NOT NULL DEFAULT 0,

    -- Composite primary key
    PRIMARY KEY (user_id, task_id),

    -- Foreign keys
    CONSTRAINT fk_uploads_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_uploads_task FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE
);