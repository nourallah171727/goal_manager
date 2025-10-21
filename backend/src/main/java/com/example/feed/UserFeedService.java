package com.example.feed;

import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class UserFeedService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public UserFeedService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }
    public Page<Goal> getFeed(Long userId, Pageable pageable) {
        User user=userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("user not found"));

        Page<Goal> followedPeopleGoals=goalRepository.findGoalsByPeopleWeFollow(user.getFollowing(),pageable);

        Page<Goal> popularGoals=goalRepository.findPopularGoals(pageable);

        List<Goal> mergedGoals=merge(followedPeopleGoals.getContent(),popularGoals.getContent());
        return new PageImpl<>(mergedGoals,pageable,mergedGoals.size());
    }
    private List<Goal> merge(List<Goal> followedPeopleGoals, List<Goal> popularGoals) {
        List<Goal> merged = new ArrayList<>();
        Random random = new Random();

        int i = 0;
        int j = 0;

        while (i < followedPeopleGoals.size() || j < popularGoals.size()) {

            if (i >= followedPeopleGoals.size()) {
                merged.add(popularGoals.get(j++));
            } else if (j >= popularGoals.size()) {
                merged.add(followedPeopleGoals.get(i++));
            } else {
                double prob = random.nextDouble();
                if (prob < 0.6) {
                    merged.add(followedPeopleGoals.get(i++));
                } else {
                    merged.add(popularGoals.get(j++));
                }
            }
        }

        return merged;
    }

}

