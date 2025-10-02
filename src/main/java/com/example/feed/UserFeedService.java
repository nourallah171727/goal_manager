package com.example.feed;

import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserFeedService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public UserFeedService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public List<Goal> getTimeLine(Long userId, int page, int size){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        // Category A: goals by people I follow
        List<Goal> categoryA = user.getFollowing().stream()
                .flatMap(f -> f.getGoals().stream())
                .filter(this::isValidGoal)
                .collect(Collectors.toList());
        sortGoals(categoryA);

        // Category B: goals from DB, paged
        Page<Goal> pageOfGoals = goalRepository.findAll(PageRequest.of(page, size));
        List<Goal> categoryB = pageOfGoals.getContent().stream()
                .filter(g -> !categoryA.contains(g))
                .filter(this::isValidGoal)
                .collect(Collectors.toList());
        sortGoals(categoryB);

        return mergeGoals(categoryA, categoryB, size); // deliver `size` goals
    }
    private boolean isValidGoal(Goal g) {
        if (g.getDueDate() == null) return true;
        return g.getDueDate().isAfter(LocalDate.now());
    }

    private void sortGoals(List<Goal> goals) {
        goals.sort(Comparator.comparingDouble(this::score).reversed());
    }

    private double score(Goal g) {
        double memberFactor = Math.log(g.getMembers().size() + 1); // more members, more value
        double timeFactor = 0.0;
        if (g.getDueDate() != null) {
            long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), g.getDueDate());
            timeFactor = daysLeft > 0 ? 1.0 / daysLeft : 0.0; // sooner = higher
        }
        return memberFactor + timeFactor;
    }

    private List<Goal> mergeGoals(List<Goal> A, List<Goal> B, int limit) {
        List<Goal> result = new ArrayList<>();
        int i = 0, j = 0;

        Random random = new Random();
        while (result.size() < limit && (i < A.size() || j < B.size())) {
            boolean pickA;
            // 70% chance to pick from A if available
            if (i < A.size() && j < B.size()) {
                pickA = random.nextDouble() < 0.7;
            } else {
                pickA = j >= B.size(); // only A left
            }

            if (pickA && i < A.size()) {
                result.add(A.get(i++));
            } else if (j < B.size()) {
                result.add(B.get(j++));
            }
        }
        return result;
    }
}

