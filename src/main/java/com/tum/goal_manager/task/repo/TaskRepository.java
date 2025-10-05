
package com.tum.goal_manager.task.repo;

import com.tum.goal_manager.goal.entity.Goal;
import com.tum.goal_manager.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByGoal_Id(Long goalId);
    List<Task> findByGoal_Id(Long goalId);

    List<Task> findByGoal(Goal goal);
}
