package com.example.demo.service;

import com.example.demo.dto.DTOMapper;
import com.example.demo.dto.UserCreateDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    //assumptions from validation layer:
    //username and email are never null
    //goals set is always initialized to an empty hashset , every attempt to add goals through user api is automatically ignored
    private final UserRepository repository;
    private final DTOMapper dtoMapper;
    @Autowired
    public UserService(UserRepository repository,DTOMapper dtoMapper){
        this.repository = repository;
        this.dtoMapper=dtoMapper;
    }
    public UserResponseDTO getUserById(Long userId){
        return dtoMapper.userToResponseDTO(repository.findById(userId).orElseThrow(()->new IllegalArgumentException("no user with such ID")));
    }
    public List<UserResponseDTO> getUsers(){
        return repository.findAll().stream().map(e->dtoMapper.userToResponseDTO(e)).collect(Collectors.toList());
    }
    public UserResponseDTO createUser(UserCreateDTO user){
        if(repository.existsByUsername(user.username())){
            throw new IllegalArgumentException("name already used");
        }
        if(repository.existsByEmail(user.email())){
            throw new IllegalArgumentException("email already used");
        }
        return dtoMapper.userToResponseDTO(repository.save(dtoMapper.createDtoToUser(user)));
    }
    public UserResponseDTO updateUser(Long id , UserUpdateDTO user){
        if(repository.findById(id).isEmpty()){
            throw new IllegalArgumentException("user should already be in db");
        }
        if(repository.existsByUsername(user.username())){
            throw new IllegalArgumentException("name already exists");
        }
        if(repository.existsByEmail(user.email())){
            throw new IllegalArgumentException("email already exists");
        }
        //setting id to truly update the user
        User userToUpdate=dtoMapper.updateDtoToUser(user);
        userToUpdate.setId(id);
        return dtoMapper.userToResponseDTO(repository.save(userToUpdate));
    }
    public void deleteById(Long id){
        if(repository.findById(id).isEmpty()){
            throw new IllegalArgumentException("user must already be in the db");
        }
        repository.deleteById(id);
    }
    public void follow(Long followerId,Long followeeId){


    }
    public void unfollow(User user){

    }
}
