package com.example.vote;

import com.example.task.entity.Task;
import com.example.task.repo.TaskRepository;
import com.example.task.service.TaskService;
import com.example.upload.entity.Upload;
import com.example.upload.entity.UploadId;
import com.example.upload.repo.UploadRepository;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VoteService {
    private final TaskRepository taskRepository;
    private final UploadRepository uploadRepository;
    private final TaskService taskService;
    private final UserRepository userRepository;
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
    public void voteFor(Long votedUserId,Long task_id){
        Task task=taskRepository.findById(task_id).orElseThrow(()->new IllegalArgumentException("task not found"));
        User user=getCurrentUser();

        if(!task.getGoal().getMembers().contains(user)){
            throw new AccessDeniedException("not authorized to vote for this file");
        }
        if(uploadRepository.findById(new UploadId(votedUserId,task_id)).isEmpty()){
            throw new IllegalArgumentException("no uploads exist for this user");
        }

        uploadRepository.incrementVotes(votedUserId,task_id);
        Upload upload=uploadRepository.findById(new UploadId(votedUserId,task_id))
                .orElseThrow(()->new IllegalArgumentException("no uploads exist for this user and task"));
        if(upload.getCurrentVotes()>=(task.getGoal().getVotesToMarkCompleted())){
            uploadRepository.delete(upload);
            taskService.markDone(task_id);
        }
    }
}
