package com.example.user.controller;

import com.example.user.dto.DTOMapper;
import com.example.user.dto.UserCreateDTO;
import com.example.user.dto.UserResponseDTO;
import com.example.user.dto.UserUpdateDTO;
import com.example.user.entity.User;
import com.example.user.service.UserService;
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
    //only users
    @GetMapping("/{userId}") // tested
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable("userId") Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(dtoMapper.userToResponseDTO(user));
    }
    //Only users
    //pagination needed
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() { // tested
        List<UserResponseDTO> result = userService.getUsers().stream()
                .map(dtoMapper::userToResponseDTO)
                .toList();
        return ResponseEntity.ok(result);
    }
    //should be anyone
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserCreateDTO userDto) { // tested
        User userEntity = dtoMapper.createDtoToUser(userDto);
        User saved = userService.createUser(userEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.userToResponseDTO(saved));
    }
    //admin or only the user itself
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable("id") Long id,
                                                      @Valid @RequestBody UserUpdateDTO userDto) {
        User userEntity = dtoMapper.updateDtoToUser(userDto);
        User updated = userService.updateUser(id, userEntity);
        return ResponseEntity.ok(dtoMapper.userToResponseDTO(updated));
    }
    //admin or only user itself
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    //only the follower
    @PostMapping("/{followerId}/follow/{followeeId}")
    public ResponseEntity<Void> follow(@PathVariable Long followerId,
                                       @PathVariable Long followeeId) {
        userService.follow(followerId, followeeId);
        return ResponseEntity.ok().build();
    }
    //only follower

    @DeleteMapping("/{followerId}/unfollow/{followeeId}")
    public ResponseEntity<Void> unfollow(@PathVariable Long followerId,
                                         @PathVariable Long followeeId) {
        userService.unfollow(followerId, followeeId);
        return ResponseEntity.noContent().build();
    }
}