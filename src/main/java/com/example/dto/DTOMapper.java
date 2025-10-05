package com.example.dto;

import com.example.dto.goal.GoalCreateDTO;
import com.example.dto.goal.GoalResponseDTO;
import com.example.dto.task.TaskCreateDTO;
import com.example.dto.task.TaskResponseDTO;
import com.example.dto.user.UserCreateDTO;
import com.example.dto.user.UserResponseDTO;
import com.example.dto.user.UserUpdateDTO;
import com.example.goal.entity.Goal;
import com.example.task.entity.Task;
import com.example.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DTOMapper {
    public User createDtoToUser(UserCreateDTO createDTO){
        User user= new User(createDTO.username(), createDTO.email());
        user.setPassword(createDTO.password());
        user.setRole("USER");
        return user;
    }
    public User updateDtoToUser(UserUpdateDTO updateDTO){
        User user= new User(updateDTO.username(), updateDTO.email());
        user.setPassword(updateDTO.password());
        user.setRole("USER");
        return user;
    }
    public UserResponseDTO userToResponseDTO(User user){
        return new UserResponseDTO(user.getId(),user.getUsername(),user.getEmail());
    }
    public Goal createDtoToGoal(GoalCreateDTO createDTO){
        Goal goal=new Goal();
        goal.setName(createDTO.name());
        goal.setCategory(createDTO.category());
        goal.setType(createDTO.type());
        goal.setPrivateCode(createDTO.privateCode());
        goal.setVotesToMarkCompleted(createDTO.votesToMarkCompleted());
        goal.setTasks(createDTO.tasks().stream().map(e->createDtoToTask(e)).collect(Collectors.toSet()));
        goal.setDueDate(createDTO.dueDate());
        return goal;
    }
    public Task createDtoToTask(TaskCreateDTO createDTO){
        Task task=new Task();
        task.setName(createDTO.name());
        task.setDifficulty(createDTO.difficulty());
        return task;
    }
    public TaskResponseDTO taskToResponseDTO(Task task){
        return new TaskResponseDTO(task.getId(),task.getName(),task.getDifficulty());
    }
    public GoalResponseDTO goalToResponseDTO(Goal goal){
        return new GoalResponseDTO(goal.getId(),goal.getName(),goal.getCategory());
    }
}
