package com.example.task.service;
import com.example.goal.entity.Goal;
import com.example.task.entity.Task;
import com.example.task.common.TaskStatus;
import com.example.user.entity.User;
import com.example.task.repo.TaskRepository;
import com.example.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//here why transactional?
@Transactional
public class TaskService {
    private final TaskRepository repository;
    @Autowired
    public TaskService(TaskRepository repository){
        this.repository = repository;
    }
    public Task createTask(String name, Goal goal){
        List<Task> tasks = repository.findByGoal(goal);
        //I think that here it should be an OR not an END
        if(tasks.stream().anyMatch(t->t.getName().equals(name) && t.getGoal().getId().equals(goal.getId()))){
            throw new IllegalArgumentException("task already exists!");
        }
        Task task = new Task(name, goal);
        return repository.save(task);
    }
    public void deleteTask(Long id){
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot delete non existing tasks"));
        repository.delete(task);
    }
    public Task updateTaskName(Long id, String name){
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot update non existing tasks"));
        task.setName(name);
        return repository.save(task);
    }
    public Task getTaskById(Long id){
        return repository.findById(id).orElseThrow(()->new IllegalArgumentException("no task with the given id"));
    }
    public List<Task> getTasksByGoalId(Long id){
        List<Task> tasks = repository.findByGoal_Id(id);
        //empty list of tasks should also be ok no ? I think if goal does not exist is a problem n bot an empty list of tasks!
        if(tasks.isEmpty()) throw new IllegalArgumentException("no task  associated to such a goal");
        return tasks;
    }
    public Task markDone(Long id){
        Task task = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("cannot update non existing tasks"));
        if(task.getTaskStatus() == TaskStatus.DONE) throw new IllegalCallerException("A fininshed task can't be remarked as done");
        task.setTaskStatus(TaskStatus.DONE);
        return repository.save(task);
    }

    @Service
    public static class CustomUserDetailsService implements UserDetailsService {
        private final UserRepository userRepository;
        @Autowired
        public CustomUserDetailsService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getEncodedPassword())
                    .roles(user.getRole())
                    .build();
        }
    }
}
