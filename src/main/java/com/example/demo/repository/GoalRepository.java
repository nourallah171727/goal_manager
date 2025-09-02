package com.example.demo.repository;


import com.example.demo.model.Goal;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    boolean existsByUser_Id(Long userId);
    List<Goal> findByUser_Id(Long userId);
    List<Goal> findByUser(User user);
}