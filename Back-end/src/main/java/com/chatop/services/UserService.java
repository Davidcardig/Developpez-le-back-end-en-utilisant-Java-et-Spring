package com.chatop.services;

import com.chatop.dtos.UserResponse;
import com.chatop.exceptions.UserNotFoundException;
import com.chatop.mappers.UserMapper;
import com.chatop.models.User;
import com.chatop.repositories.UserRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserResponse getUserById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(UserMapper::toResponse).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}

