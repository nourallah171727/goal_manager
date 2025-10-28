package com.example.feed;

import com.example.dto.DTOMapper;
import com.example.dto.goal.GoalFeedDTO;
import com.example.goal.entity.Goal;
import com.example.goal.repo.GoalRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GoalPopularityBasedFeedService implements UserFeedService{
    private GoalRepository goalRepository;
    private DTOMapper dtoMapper;
    private UserRepository userRepository;
    @Autowired
    public GoalPopularityBasedFeedService(GoalRepository goalRepository,DTOMapper dtoMapper,UserRepository userRepository){
        this.goalRepository=goalRepository;
        this.dtoMapper=dtoMapper;
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
    public Page<GoalFeedDTO> getFeedForUser(Long userId ,int page , int size ){
        if(!getCurrentUser().getId().equals(userId)){
            throw new AccessDeniedException("the feeds you requested are for some other user!");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Goal> goals = goalRepository.findMostPopularGoals(pageable);
        return goals.map(e->dtoMapper.goalToGoalFeedDTO(e));
    }
    public String getType(){
        return "popularityBased";
    }
}
