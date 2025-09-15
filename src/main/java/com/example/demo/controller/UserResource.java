package com.example.demo.controller;

import com.example.demo.dto.DTOMapper;
import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserResource {
    private final UserService userService;
    private final DTOMapper dtoMapper;

    @Autowired
    public UserResource(UserService userService, DTOMapper dtoMapper) {
        this.userService = userService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/{userId}") //tested
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable("userId") Long userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(dtoMapper.userToResponseDTO(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() { //tested
        List<UserResponseDTO> result = userService.getUsers().stream()
                .map(dtoMapper::userToResponseDTO)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userDto) { //tested
        try {
            User userEntity = dtoMapper.createDtoToUser(userDto);
            User saved = userService.createUser(userEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.userToResponseDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable("id") Long id,
                                                      @Valid @RequestBody UserUpdateDTO userDto) {
        try {
            User userEntity = dtoMapper.updateDtoToUser(userDto);
            User updated = userService.updateUser(id, userEntity);
            return ResponseEntity.ok(dtoMapper.userToResponseDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/{followerId}/follow/{followeeId}")
    public ResponseEntity<Void> follow(
            @PathVariable Long followerId,
            @PathVariable Long followeeId) {
        try {
            userService.follow(followerId, followeeId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public ResponseEntity<Void> unfollow(
            @PathVariable Long followerId,
            @PathVariable Long followeeId) {
        try {
            userService.unfollow(followerId, followeeId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}