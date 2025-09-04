package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserResource {
    private UserService userService;
    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable("userId") Long userId) {
        try {
            User user=userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User userDetails) {
        try{
            return ResponseEntity.ok(userService.updateUser(id , userDetails));
        } catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        try{
            userService.deleteById(id);
            return ResponseEntity.noContent().build();

        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
