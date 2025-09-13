package com.example.demo.dto;

import com.example.demo.model.User;
import org.springframework.stereotype.Component;
@Component
public class DTOMapper {
    public User createDtoToUser(UserCreateDTO createDTO){
        return new User(createDTO.username(), createDTO.email());
    }
    public User updateDtoToUser(UserUpdateDTO updateDTO){
        return new User(updateDTO.username(), updateDTO.email());

    }
    public User responseDtoToUser(UserResponseDTO responseDTO){
        return new User(responseDTO.username(),responseDTO.email());
    }
    public UserCreateDTO userToCreateDTO(User user){
        return new UserCreateDTO(user.getUsername(),user.getEmail());
    }
    public UserUpdateDTO userToUpdateDTO(User user){
        return new UserUpdateDTO(user.getUsername(),user.getEmail());
    }
    public UserResponseDTO userToResponseDTO(User user){
        return new UserResponseDTO(user.getUsername(),user.getEmail());
    }
}
