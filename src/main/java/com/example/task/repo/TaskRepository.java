
package com.example.task.repo;

import com.example.model.Goal;
import com.example.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByGoal_Id(Long goalId);
    List<Task> findByGoal_Id(Long goalId);

    List<Task> findByGoal(Goal goal);
}
