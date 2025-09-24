CREATE TABLE user_finished_tasks (
    user_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    finished_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, task_id),
    CONSTRAINT fk_user_finished_tasks_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_finished_tasks_task FOREIGN KEY (task_id)
        REFERENCES tasks(task_id) ON DELETE CASCADE
);