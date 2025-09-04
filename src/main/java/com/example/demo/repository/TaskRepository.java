
package com.example.demo.repository;

import com.example.demo.model.Goal;
import com.example.demo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    boolean existsByGoal_Id(Long goalId);
    List<Task> findByGoal_Id(Long goalId);

    List<Task> findByGoal(Goal goal);
}
