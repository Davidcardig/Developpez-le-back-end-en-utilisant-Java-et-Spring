package com.chatop.services;

import com.chatop.configuration.JwtUtils;
import com.chatop.dtos.AuthResponse;
import com.chatop.dtos.LoginRequest;
import com.chatop.dtos.RegisterRequest;
import com.chatop.dtos.UserResponse;
import com.chatop.mappers.UserMapper;
import com.chatop.models.User;
import com.chatop.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse registerUser(RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getFieldErrors().stream()
                    .findFirst()
                    .map(f -> f.getDefaultMessage())
                    .orElse("Invalid request");
            throw new IllegalArgumentException(error);
        }

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already used");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        User saved = userRepository.save(user);
        String token = jwtUtils.generateToken(saved.getEmail());

        return new AuthResponse(token);
    }

    public Map<String, Object> loginUser(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Map<String, Object> authData = new HashMap<>();
        authData.put("token", jwtUtils.generateToken(request.getEmail()));
        authData.put("type", "Bearer");

        return authData;
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return UserMapper.toResponse(user);
    }

    public String extractEmailFromToken(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing Authorization header");
        }

        String token;
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7).trim();
        } else {
            token = authorization.trim();
        }

        return jwtUtils.extractName(token);
    }
}

