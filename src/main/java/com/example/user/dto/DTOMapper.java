package com.example.user.dto;

import com.example.user.entity.User;
import org.springframework.stereotype.Component;
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
}
