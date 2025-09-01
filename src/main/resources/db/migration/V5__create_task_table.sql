
CREATE TYPE task_status AS ENUM ('DONE', 'NOT_DONE');

CREATE TABLE IF NOT EXISTS tasks (
    task_id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    status task_status NOT NULL DEFAULT 'NOT_DONE',
    task_goal BIGINT NOT NULL REFERENCES goals(id)
);
