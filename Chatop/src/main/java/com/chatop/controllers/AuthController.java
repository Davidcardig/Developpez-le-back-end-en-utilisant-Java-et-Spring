package com.chatop.controllers;

import com.chatop.dtos.*;
import com.chatop.services.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }


  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest req, org.springframework.validation.BindingResult bindingResult) {
    try {
      Map<String, Object> data = authService.registerUser(req, bindingResult);
      return ResponseEntity.ok(data);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }


  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@RequestBody LoginRequest req) {
    try {
      Map<String, Object> authData = authService.loginUser(req);
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

    if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
      email = auth.getName();
    } else {
      try {
        email = authService.extractEmailFromToken(authorization);
      } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
      }
    }

    try {
      UserResponse userResponse = authService.getCurrentUser(email);
      return ResponseEntity.ok(userResponse);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }
  }
}
