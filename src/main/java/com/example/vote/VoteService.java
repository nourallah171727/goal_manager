package com.example.vote;

import com.example.task.entity.Task;
import com.example.task.repo.TaskRepository;
import com.example.task.service.TaskService;
import com.example.upload.entity.Upload;
import com.example.upload.entity.UploadId;
import com.example.upload.repo.UploadRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class VoteService {
    private final TaskRepository taskRepository;
    private final UploadRepository uploadRepository;
    private final TaskService taskService;
    private final UserRepository userRepository;
    private final ConcurrentHashMap<UploadId, Object> uploadLocks = new ConcurrentHashMap<>();
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    public VoteService(TaskRepository taskRepository,UploadRepository uploadRepository,TaskService taskService,UserRepository userRepository){
        this.taskRepository=taskRepository;
        this.uploadRepository=uploadRepository;
        this.taskService=taskService;
        this.userRepository=userRepository;
    }
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AccessDeniedException("User not found"));
    }
    //we shouldn't allow user to send vote requests for himself to implement later!
    //for simplicity now , we allow the same user to vote for same file more than once
    public void voteFor(Long votedUserId, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("task not found"));

        User user = getCurrentUser();

        if (!task.getGoal().getMembers().contains(user)) {
            throw new AccessDeniedException("not authorized to vote for this file");
        }

        UploadId key = new UploadId(votedUserId, taskId);

        if (uploadRepository.findById(key).isEmpty()) {
            throw new IllegalArgumentException("no uploads exist for this user");
        }

        int isUploadedSuccessfully=uploadRepository.incrementVotesIfBelowThreshold(votedUserId, taskId,task.getGoal().getVotesToMarkCompleted());

        entityManager.clear();

        Upload upload=uploadRepository.findById(key).orElseThrow();
        if(isUploadedSuccessfully==1 && upload.getCurrentVotes()==task.getGoal().getVotesToMarkCompleted()){
            uploadRepository.delete(upload);
            taskService.markDone(taskId,votedUserId);
        }

    }
}
