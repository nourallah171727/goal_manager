package com.example.user.dto;

import com.example.user.entity.User;
import org.springframework.stereotype.Component;
@Component
public class DTOMapper {
    public User createDtoToUser(UserCreateDTO createDTO){
        User user= new User(createDTO.username(), createDTO.email());
        user.setRole("USER");
        return user;
    }
    public User updateDtoToUser(UserUpdateDTO updateDTO){
        return new User(updateDTO.username(), updateDTO.email());

    }
    public UserResponseDTO userToResponseDTO(User user){
        return new UserResponseDTO(user.getUsername(),user.getEmail());
    }
}
