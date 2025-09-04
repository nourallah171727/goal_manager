package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    private final UserRepository repository;
    @Autowired
    public UserService(UserRepository repository){
        this.repository = repository;
    }
    public User getUserById(Long userId){
        if(userId==null){
            throw new IllegalArgumentException("userId is null");
        }
        return repository.findById(userId).orElseThrow(()->new IllegalArgumentException("no user with such ID"));
    }
    public List<User> getUsers(){
        return repository.findAll();
    }
    public User createUser(User user){
        if(user==null){
            throw new IllegalArgumentException("user should not be null");
        }
        if(user.getId()!=null){
            throw new IllegalArgumentException("user should not already have an ID!");
        }
        return repository.save(user);
    }
    public User updateUser(Long id , User user){
        if(user==null){
            throw new IllegalArgumentException("user should not be null");
        }
        if(user.getId()==null || repository.findById(id).isEmpty()){
            throw new IllegalArgumentException("user must already be in the db");
        }
        return repository.save(user);
    }
    public void deleteById(Long id){
        if(id==null || repository.findById(id).isEmpty()){
            throw new IllegalArgumentException("user must already be in the db");
        }
        repository.deleteById(id);
    }
}
