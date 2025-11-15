package com.chatop.controllers;


import com.chatop.configuration.JwtUtils;
import com.chatop.dtos.*;
import com.chatop.mappers.UserMapper;
import com.chatop.models.User;
import com.chatop.repositories.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;

  public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtils = jwtUtils;
    this.authenticationManager = authenticationManager;
  }


  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest req, org.springframework.validation.BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      // retourner la première erreur de validation pour la simplicité
      String err = bindingResult.getFieldErrors().stream().findFirst().map(f -> f.getDefaultMessage()).orElse("Invalid request");
      return ResponseEntity.badRequest().body(err);
    }

    // vérifier si l'email est déjà utilisé
    if (userRepository.findByEmail(req.getEmail()) != null) {
      return ResponseEntity.badRequest().body("Email is already used");
    }


    User user = new User();
    user.setName(req.getName());
    user.setEmail(req.getEmail());
    user.setPassword(req.getPassword());

    // encoder le mot de passe avant de sauvegarder
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    // assigner un rôle par défaut si absent
    if (req.getRole() == null || req.getRole().trim().isEmpty()) {
      user.setRole("ROLE_USER");
    } else {
      user.setRole(req.getRole());
    }

    //générer un token d'authentification pour le nouvel utilisateur
    User saved = userRepository.save(user);
    UserResponse resp = UserMapper.toResponse(saved);

    // générer un token pour la réponse d'inscription
    Map<String, Object> data = new HashMap<>();
    data.put("token", jwtUtils.generateToken(saved.getEmail()));
    data.put("type", "Bearer");
    data.put("user", resp);
    return ResponseEntity.ok(data);

  }


  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequest req) {
    try {

      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

      Map<String, Object> authData = new HashMap<>();
      authData.put("token", jwtUtils.generateToken(req.getEmail()));
      authData.put("type", "Bearer");
      return ResponseEntity.ok(authData);
    } catch (Exception e) {
      System.err.println("Authentication error: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
    }
  }



  @GetMapping("/me")
  public ResponseEntity<?> getMe(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email;
    String token;
    if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
      email = auth.getName();
    } else {
      if (authorization == null || authorization.trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing Authorization header");
      }


      if (authorization.startsWith("Bearer ")) {
        token = authorization.substring(7).trim();
      } else {
        token = authorization.trim();
      }

      try {
        email = jwtUtils.extractName(token);
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
      }
    }

    User user = userRepository.findByEmail(email);
    UserResponse resp = UserMapper.toResponse(user);
    return ResponseEntity.ok(resp);
  }
}
