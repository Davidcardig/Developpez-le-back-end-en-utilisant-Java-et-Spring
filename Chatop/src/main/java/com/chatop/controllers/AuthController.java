package com.chatop.controllers;

import com.chatop.dtos.*;
import com.chatop.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentification", description = "API de gestion de l'authentification des utilisateurs")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }


  @Operation(
    summary = "Inscription d'un nouvel utilisateur",
    description = "Créer un nouveau compte utilisateur avec email, nom et mot de passe"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Utilisateur créé avec succès",
      content = @Content(mediaType = "application/json")
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Requête invalide - données manquantes ou incorrectes",
      content = @Content
    )
  })
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest req, org.springframework.validation.BindingResult bindingResult) {
    try {
      Map<String, Object> data = authService.registerUser(req, bindingResult);
      return ResponseEntity.ok(data);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }


  @Operation(
    summary = "Connexion d'un utilisateur",
    description = "Authentifie un utilisateur et retourne un token JWT"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Authentification réussie",
      content = @Content(mediaType = "application/json")
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Identifiants invalides",
      content = @Content
    )
  })
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


  @Operation(
    summary = "Récupérer les informations de l'utilisateur connecté",
    description = "Retourne les informations de l'utilisateur actuellement authentifié",
    security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Informations utilisateur récupérées avec succès",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = UserResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Non autorisé - token invalide ou manquant",
      content = @Content
    )
  })
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
