package com.example.feed;

import com.example.dto.goal.GoalFeedDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class FollowerBasedFeedService implements UserFeedService{
    public Page<GoalFeedDTO> getFeedForUser(Long userId, int page , int size){
        return null;
    }
    public String getType(){
        return "followerBased";
    }
}
