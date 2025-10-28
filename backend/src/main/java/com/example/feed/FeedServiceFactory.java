package com.example.feed;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedServiceFactory {
    private final Map<String, UserFeedService> feedServices;

    public FeedServiceFactory(List<UserFeedService> feedServices) {
        this.feedServices = feedServices.stream()
                .collect(Collectors.toMap(UserFeedService::getType, s -> s));
    }

    public UserFeedService getService(String type) {
        if(!feedServices.containsKey(type)){
            throw new IllegalArgumentException("this type is not supported");
        }
        return feedServices.get(type);
    }
}
