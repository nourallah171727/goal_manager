package com.example.demo.service;
import com.example.demo.model.Goal;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//here why transactional? chat 3tahali 9allek rollbak ki tsir exception
//bon houa rahou fibeli mech les méthodes lkol lezem transactional fiha 5orrefa
//nhessha hakka safe ama bati2a

@Transactional
public class TaskService {
    private final TaskRepository repository;
    @Autowired
    public TaskService(TaskRepository repository){
        this.repository = repository;
    }
    public Task createTask(String name, Goal goal){
        List<Task> tasks = repository.findByGoal(goal);
        //I think that here it should be an OR not an END: doch lazem And 5ater sinon tnajjem tkoun nafs l esm ama l goal e5er konchi itha mathabbech terlaubi zouz taskouet l goals mo5talfin 3andhom nafs l esm
        // houni de toute façon l or 8alta 5ater ki thott or ywalli ma3andekch l 7a9 zouz taskouet 3andhom nafs l esm
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
        // hattana 5ammemt fiha c'est un choix ena 3maltou 9olt ashel bech na3rfou ennou l goal heka ma3andouch taskouet
        // thebb nbaddlouha 3adi
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
}
