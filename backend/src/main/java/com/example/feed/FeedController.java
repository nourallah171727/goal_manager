package com.example.feed;

import com.example.dto.goal.GoalFeedDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public class FeedController {
    private FeedServiceFactory feedServiceFactory;
    public FeedController(FeedServiceFactory feedServiceFactory){
        this.feedServiceFactory=feedServiceFactory;
    }
    @GetMapping("/{userId}")
    public ResponseEntity<Page<GoalFeedDTO>> getUserFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "popularityBased") String type
    ) {
        UserFeedService feedService = feedServiceFactory.getService(type); // factory pattern
        Page<GoalFeedDTO> feedPage = feedService.getFeedForUser(userId, page, size);
        return ResponseEntity.ok(feedPage);
    }
}
