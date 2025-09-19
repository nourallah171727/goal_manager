package com.example.goal.repo;


import com.example.goal.entity.Goal;
import com.example.goal.common.GoalType;
import com.example.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    boolean existsByHost_Id(Long userId);
    List<Goal> findByHost_Id(Long userId);
    List<Goal> findByHost(User user);
    List<Goal> findByMembersContaining(User user);
    List<Goal> findByType(GoalType type);
    List<Goal> findByCategory(String category);
}