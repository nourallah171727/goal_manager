package com.example.feed;

import com.example.dto.goal.GoalFeedDTO;
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


public interface UserFeedService {
    Page<GoalFeedDTO> getFeedForUser(Long userId , int page , int size);
    String getType();
}

