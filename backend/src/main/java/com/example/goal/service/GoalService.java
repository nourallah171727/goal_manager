package com.example.goal.service;

import com.example.goal.common.GoalStand;
import com.example.goal.entity.Goal;
import com.example.goal.common.GoalType;
import com.example.ranking.repo.UserScorePairRepository;
import com.example.ranking.service.UserScorePairService;
import com.example.task.entity.Task;
import com.example.user.entity.User;
import com.example.goal.repo.GoalRepository;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final UserScorePairService userScorePairService;


    @Autowired
    public GoalService(GoalRepository goalRepository,UserRepository userRepository,UserScorePairService userScorePairService) {
        this.userScorePairService=userScorePairService;
        this.goalRepository = goalRepository;
        this.userRepository=userRepository;
    }
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new AccessDeniedException("No authentication found");
        }
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    public Goal getGoalById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("goalId is null") ;
        }
        return goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("no goal with such ID"));
    }

    public List<Goal> getGoals(Long userId) {
        return goalRepository.findAll();
    }

    public Goal createGoal(Goal goal,Long userId) {
        if(userId==null){
            throw new IllegalArgumentException("userId should not be null");
        }
        Optional<User> optUser=userRepository.findById(userId);
        if(optUser.isEmpty()){
            throw new IllegalArgumentException("userId should be a valid Id");
        }
        if (goal == null) {
            throw new IllegalArgumentException("goal should not be null");
        }
        if (goal.getId() != null) {
            throw new IllegalArgumentException("goal should not already have an ID!");
        }
        goal.setHost(optUser.get());
        if(goal.getType()!= GoalType.PRIVATE){
            if(goal.getPrivateCode()!=null){
                throw new IllegalArgumentException("public goals should have no password");
            }
        }else{
            Objects.requireNonNull(goal.getPrivateCode(),"private goals should have a password");
        }
        if(goal.getTasks().isEmpty()){
            throw new IllegalArgumentException("freshly created goals should at least have one task!");
        }
        int totalPoints=0;
        for(Task task:goal.getTasks()){
            task.setGoal(goal);
            totalPoints+=task.getDifficulty().getWeight();
        }
        goal.setTotalPoints(totalPoints);
        Goal saved=goalRepository.save(goal);
        userScorePairService.joinGoal(saved.getId(),userId);
        return saved;
    }
/*
    public Goal updateGoal(Long id, Goal goal) {
        if(id==null){
            throw new IllegalArgumentException("id must not be null");
        }
        if (goal == null) {
            throw new IllegalArgumentException("goal should not be null");
        }
        if(goal.getId()!=null){
            throw new IllegalArgumentException("goal object shall not contain an id , you must update only through path variable");
        }
        Goal existingGoal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("goal must already be in the db"));
        User currentUser = getCurrentUser();

        boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getRole());
        boolean isHost = existingGoal.getHost().getId().equals(currentUser.getId());
        if (!isAdmin && !isHost) {
            throw new AccessDeniedException("You are not allowed to update this goal");
        }
        if(LocalDate.now().isAfter(goal.getDueDate())){
            throw new IllegalStateException("can't updated an expired goal");
        }


        goal.setId(id);

        return goalRepository.save(goal);
    }
    */


    public void deleteById(Long id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("goal must already be in the db"));

        User currentUser = getCurrentUser();

        boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getRole());
        boolean isHost = goal.getHost().getId().equals(currentUser.getId());

        if (!isAdmin && !isHost) {
            throw new AccessDeniedException("You are not allowed to delete this goal");
        }

        goalRepository.deleteById(id);
    }



    //this is to fix , u cannot join goals that are actually private
    public void joinGoal(Long goalId, Long userId) {
        User user=getCurrentUser();
        if(!user.getId().equals(userId)){
            throw new AccessDeniedException("not authorized");
        }
        Goal goal=goalRepository.findById(goalId).orElseThrow(()->new IllegalArgumentException("goal not exist"));
        if(userRepository.findById(userId).isEmpty()){
            throw new IllegalArgumentException("either goal or user do not exist!");
        }
        if(goal.getDueDate()!=null && (goal.getDueDate().isBefore(LocalDate.now()))){
            throw new IllegalStateException("can't join a goal that's expired");
        }
        userScorePairService.joinGoal(goalId,userId);

    }
    public void leaveGoal(Long goalId, Long userId) {
        if(goalRepository.findById(goalId).isEmpty() || userRepository.findById(userId).isEmpty()){
            throw new IllegalArgumentException("either goal or user do not exist!");
        }
        User user=getCurrentUser();
        Goal goal=goalRepository.findById(goalId).orElseThrow(()->new IllegalArgumentException("goal not exist"));
        if(userRepository.findById(userId).isEmpty()){
            throw new IllegalArgumentException("either goal or user do not exist!");
        }
        if(goal.getDueDate()!=null && goal.getDueDate().isBefore(LocalDate.now())){
            throw new IllegalStateException("can't join a goal that's expired");
        }
        userScorePairService.leaveGoal(userId,goalId);
    }
    /*
    public void addStar(Long goalId, Long userId) {
        Goal goal = getGoalById(goalId);
        User user = validateAndGetUser(userId);
        goal.getStarredBy().add(user);
        goalRepository.save(goal);
    }

    public void removeStar(Long goalId, Long userId) {
        Goal goal = getGoalById(goalId);
        User user = validateAndGetUser(userId);
        goal.getStarredBy().remove(user);
        goalRepository.save(goal);
    }*/

    public List<Goal> findGoalsByCategory(String category) {
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }
        return goalRepository.findByCategory(category);
    }

    public List<Goal> findGoalsByType(GoalType type) {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        return goalRepository.findByType(type);
    }

    public List<Goal> findGoalsByHost(Long hostId) {
        if (hostId == null) {
            throw new IllegalArgumentException("hostId must not be null");
        }
        Optional<User> optHost = userRepository.findById(hostId);
        if (optHost.isEmpty()) {
            throw new IllegalArgumentException("hostId must be valid");
        }
        return goalRepository.findByHost(optHost.get());
    }

    public List<Goal> findGoalsByMember(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        Optional<User> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            throw new IllegalArgumentException("userId must be valid");
        }
        return goalRepository.findByMembersContaining(optUser.get());
    }
}