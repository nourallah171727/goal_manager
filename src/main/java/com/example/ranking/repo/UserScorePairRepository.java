package com.example.ranking.repo;

import com.example.ranking.model.UserScorePair;
import com.example.ranking.model.UserScorePairId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserScorePairRepository extends JpaRepository<UserScorePair, UserScorePairId> {

    List<UserScorePair> findByGoalIdOrderByScoreDesc(Long goalId, Pageable pageable);
    Optional<UserScorePair> findByGoalIdAndUserId(Long goalId, Long userId);
    @Modifying
    @Query("UPDATE UserScorePair usp " +
            "SET usp.score = usp.score + :weight " +
            "WHERE usp.goalId = :goalId AND usp.userId = :userId")
    void incrementScore(@Param("goalId") Long goalId,
                       @Param("userId") Long userId,
                       @Param("weight") int weight);
}