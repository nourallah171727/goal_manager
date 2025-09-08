package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserService {
    //assumptions from validation layer:
    //username and email are never null
    //goals set is always initialized to an empty hashset , every attempt to add goals through user api is automatically ignored
    private final UserRepository repository;
    @Autowired
    public UserService(UserRepository repository){
        this.repository = repository;
    }
    public void  validateUser(User user){
        if(user==null || user.getId()!=null ){
            throw new IllegalArgumentException("user is not valid");
        }
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
        validateUser(user);
        if(repository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("name already used");
        }
        if(repository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("email already used");
        }
        return repository.save(user);
    }
    public User updateUser(Long id , User user){
        validateUser(user);
        if(repository.findById(id).isEmpty()){
            throw new IllegalArgumentException("user should already be in db");
        }
        if(repository.existsByUsername(user.getUsername())){
            throw new IllegalArgumentException("name already exists");
        }
        if(repository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("email already exists");
        }
        return repository.save(user);
    }
    public void deleteById(Long id){
        if(repository.findById(id).isEmpty()){
            throw new IllegalArgumentException("user must already be in the db");
        }
        repository.deleteById(id);
    }
}
