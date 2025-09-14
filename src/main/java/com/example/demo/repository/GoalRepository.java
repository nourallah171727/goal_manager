package com.example.demo.repository;


import com.example.demo.model.Goal;
import com.example.demo.model.GoalType;
import com.example.demo.model.User;
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