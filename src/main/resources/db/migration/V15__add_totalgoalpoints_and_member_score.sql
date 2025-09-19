-- Add column to store the total points of a goal
ALTER TABLE goals
    ADD COLUMN totalgoalpoints INT NOT NULL DEFAULT 0 ;

-- Add column to store the score of each user per goal
ALTER TABLE goal_members
    ADD COLUMN score INT NOT NULL DEFAULT 0 ;