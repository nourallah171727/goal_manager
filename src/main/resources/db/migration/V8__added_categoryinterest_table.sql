use goal_manager;
CREATE TABLE user_categories (
     user_id BIGINT NOT NULL,
     category ENUM('SPORTS', 'STUDY', 'HEALTH', 'WORK', 'TRAVEL') NOT NULL,
     PRIMARY KEY (user_id, category),
     CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
 ) ENGINE=InnoDB;