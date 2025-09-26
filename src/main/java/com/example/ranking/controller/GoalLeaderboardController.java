package com.example.ranking.controller;

import com.example.ranking.service.GoalLeaderboardService;
import com.example.ranking.model.UserGoalScorePair;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class GoalLeaderboardController {

    private final GoalLeaderboardService leaderboardService;

    public GoalLeaderboardController(GoalLeaderboardService leaderboardService){
        this.leaderboardService = leaderboardService;
    }

    // Example: GET /leaderboard/5/top?k=10
    @GetMapping("/{goalId}/top")
    public List<UserGoalScorePair> getTopK(@PathVariable Long goalId,
                                           @RequestParam(defaultValue = "10") int k) {


        return leaderboardService.topK(k, goalId);
    }
}