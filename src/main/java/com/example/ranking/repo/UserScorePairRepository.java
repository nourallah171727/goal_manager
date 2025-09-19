package com.example.ranking.repo;

import com.example.ranking.model.UserScorePair;
import com.example.ranking.model.UserScorePairId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserScorePairRepository extends JpaRepository<UserScorePair, UserScorePairId> {

    List<UserScorePair> findByGoalIdOrderByScoreDesc(Long goalId, Pageable pageable);
}