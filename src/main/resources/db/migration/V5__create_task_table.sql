CREATE TABLE tasks (
    task_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    status ENUM('DONE', 'NOT_DONE') NOT NULL DEFAULT 'NOT_DONE',
    task_goal BIGINT ,
    CONSTRAINT fk_task_goal FOREIGN KEY (task_goal) REFERENCES goals(goal_id)
);
