package com.example.user.dto;

import com.example.model.User;
import org.springframework.stereotype.Component;
@Component
public class DTOMapper {
    public User createDtoToUser(UserCreateDTO createDTO){
        return new User(createDTO.username(), createDTO.email());
    }
    public User updateDtoToUser(UserUpdateDTO updateDTO){
        return new User(updateDTO.username(), updateDTO.email());

    }
    public UserResponseDTO userToResponseDTO(User user){
        return new UserResponseDTO(user.getUsername(),user.getEmail());
    }
}
