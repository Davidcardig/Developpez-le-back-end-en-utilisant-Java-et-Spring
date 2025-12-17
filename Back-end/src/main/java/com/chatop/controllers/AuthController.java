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
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest req, org.springframework.validation.BindingResult bindingResult) {
    return ResponseEntity.ok(authService.registerUser(req, bindingResult));
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
  public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequest req) {
    return ResponseEntity.ok(authService.loginUser(req));
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
  public ResponseEntity<UserResponse> getMe() {
    return ResponseEntity.ok(authService.getCurrentUser());
  }
}
