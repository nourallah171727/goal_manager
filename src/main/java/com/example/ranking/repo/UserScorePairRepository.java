package com.example.ranking.repo;

import com.example.goal.entity.Goal;
import com.example.ranking.model.UserGoalScorePair;
import com.example.ranking.model.UserGoalScorePairId;
import com.example.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserScorePairRepository extends JpaRepository<UserGoalScorePair, UserGoalScorePairId> {

    List<UserGoalScorePair> findByGoalIdOrderByScoreDesc(Long goalId, Pageable pageable);
    Optional<UserGoalScorePair> findByGoalIdAndUserId(Long goalId, Long userId);
    @Modifying
    @Query("UPDATE UserGoalScorePair usp " +
            "SET usp.score = usp.score + :weight " +
            "WHERE usp.goalId = :goalId AND usp.userId = :userId")
    void incrementScore(@Param("goalId") Long goalId,
                       @Param("userId") Long userId,
                       @Param("weight") int weight);
    @Modifying
    @Query("INSERT INTO UserGoalScorePair(goalId, userId, score) " +
            "SELECT :goalId, :userId, 0 " +
            "WHERE NOT EXISTS (SELECT 1 FROM UserGoalScorePair u " +
            "                  WHERE u.goalId = :goalId AND u.userId = :userId)")
    void joinGoal(@Param("goalId") Long goalId, @Param("userId") Long userId);
    @Modifying
    @Query("DELETE FROM UserGoalScorePair u " +
            "WHERE u.goalId = :goalId AND u.userId = :userId")
    void leaveGoal(@Param("goalId") Long goalId, @Param("userId") Long userId);
    boolean existsByUserIdAndGoalId( Long userId,Long goalId);
}