package com.tum.goal_manager.ranking.repo;

import com.tum.goal_manager.ranking.model.UserGoalScorePair;
import com.tum.goal_manager.ranking.model.UserGoalScorePairId;
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
    boolean existsByUserIdAndGoalId( Long userId,Long goalId);
}