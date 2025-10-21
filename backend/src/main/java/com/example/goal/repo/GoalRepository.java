package com.example.goal.repo;


import com.example.goal.entity.Goal;
import com.example.goal.common.GoalType;
import com.example.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    boolean existsByHost_Id(Long userId);
    List<Goal> findByHost_Id(Long userId);
    List<Goal> findByHost(User user);
    List<Goal> findByMembersContaining(User user);
    List<Goal> findByType(GoalType type);
    List<Goal> findByCategory(String category);
    @Modifying
    @Query("UPDATE Goal g " +
            "SET g.totalPoints = g.totalPoints + :points " +
            "WHERE g.id = :goalId")
    void incrementTotalPoints(@Param("goalId") Long goalId,
                              @Param("points") int points);
    Page<Goal> findAll(Pageable pageable);

    @Query("SELECT DISTINCT g FROM Goal g JOIN g.members m " +
            "WHERE m IN :followedPeople")
    Page<Goal> findGoalsByPeopleWeFollow(@Param("followers") Set<User> followedPeople, Pageable pageable);

    @Query("SELECT g FROM Goal g ORDER BY SIZE(g.members) DESC")
    Page<Goal> findPopularGoals(Pageable pageable);
}