package com.chatop.services;

import com.chatop.dtos.UserResponse;
import com.chatop.mappers.UserMapper;
import com.chatop.models.User;
import com.chatop.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Récupère un utilisateur par son ID
     * @param id L'identifiant de l'utilisateur
     * @return UserResponse ou null si non trouvé
     */
    public UserResponse getUserById(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.map(UserMapper::toResponse).orElse(null);
    }

    /**
     * Récupère un utilisateur par son email
     * @param email L'email de l'utilisateur
     * @return UserResponse ou null si non trouvé
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return UserMapper.toResponse(user);
    }
}

