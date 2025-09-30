package com.example.vote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private final VoteService voteService;
    public VoteController(VoteService voteService){
        this.voteService=voteService;
    }
    @PostMapping("/{taskId}/user/{votedUserId}")
    public ResponseEntity<Void> voteForUpload(@PathVariable Long taskId,
                                              @PathVariable Long votedUserId) {
        voteService.voteFor(votedUserId, taskId);
        return ResponseEntity.ok().build();
    }
}
