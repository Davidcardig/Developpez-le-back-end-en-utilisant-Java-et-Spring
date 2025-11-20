package com.chatop.controllers;

import com.chatop.dtos.RentalDto;
import com.chatop.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Locations", description = "API de gestion des locations immobilières")
public class RentalsController {

  private final RentalService rentalService;

  public RentalsController(RentalService rentalService) {
    this.rentalService = rentalService;
  }

  @Operation(
    summary = "Récupérer toutes les locations",
    description = "Retourne la liste complète de toutes les locations disponibles",
    security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Liste des locations récupérée avec succès",
      content = @Content(mediaType = "application/json")
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Non autorisé",
      content = @Content
    )
  })
  @GetMapping
  public ResponseEntity<Map<String, Object>> getAllRentals() {
    List<RentalDto> rentals = rentalService.getAllRentals();
    return ResponseEntity.ok(Collections.singletonMap("rentals", rentals));
  }

  @Operation(
    summary = "Récupérer une location par son ID",
    description = "Retourne les détails d'une location spécifique",
    security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Location trouvée",
      content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = RentalDto.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Location non trouvée",
      content = @Content
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Non autorisé",
      content = @Content
    )
  })
  @GetMapping("/{id}")
  public ResponseEntity<?> getRentalById(
    @Parameter(description = "ID de la location", required = true)
    @PathVariable("id") Long id) {
    Optional<RentalDto> rental = rentalService.getRentalById(id);
    if (rental.isEmpty()) {
      return ResponseEntity.status(404).body("Rental not found");
    }
    return ResponseEntity.ok(rental.get());
  }

  @Operation(
    summary = "Créer une nouvelle location",
    description = "Créer une nouvelle location avec nom, surface, prix, photo et description",
    security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Location créée avec succès",
      content = @Content(mediaType = "application/json")
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Non autorisé",
      content = @Content
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Erreur lors de l'enregistrement de l'image",
      content = @Content
    )
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createRental(
    @Parameter(description = "Nom de la location", required = true)
    @RequestParam("name") String name,
    @Parameter(description = "Surface en m²", required = true)
    @RequestParam("surface") Double surface,
    @Parameter(description = "Prix de location", required = true)
    @RequestParam("price") Double price,
    @Parameter(description = "Photo de la location")
    @RequestParam(value = "picture", required = false) MultipartFile picture,
    @Parameter(description = "Description détaillée de la location")
    @RequestParam(value = "description", required = false) String description
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    String email = authentication.getName();

    try {
      RentalDto rental = rentalService.createRental(name, surface, price, picture, description, email);
      return ResponseEntity.ok(Collections.singletonMap("rental", rental));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(401).body(e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Failed to store picture: " + e.getMessage());
    }
  }


  @Operation(
    summary = "Mettre à jour une location",
    description = "Modifier les informations d'une location existante",
    security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Location mise à jour avec succès",
      content = @Content(mediaType = "application/json")
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Non autorisé",
      content = @Content
    ),
    @ApiResponse(
      responseCode = "403",
      description = "Accès interdit - vous n'êtes pas le propriétaire de cette location",
      content = @Content
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Location non trouvée",
      content = @Content
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Erreur lors de l'enregistrement de l'image",
      content = @Content
    )
  })
  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> updateRental(
    @Parameter(description = "ID de la location à modifier", required = true)
    @PathVariable("id") Long id,
    @Parameter(description = "Nouveau nom de la location", required = true)
    @RequestParam("name") String name,
    @Parameter(description = "Nouvelle surface en m²", required = true)
    @RequestParam("surface") Double surface,
    @Parameter(description = "Nouveau prix de location", required = true)
    @RequestParam("price") Double price,
    @Parameter(description = "Nouvelle photo de la location")
    @RequestParam(value = "picture", required = false) MultipartFile picture,
    @Parameter(description = "Nouvelle description de la location")
    @RequestParam(value = "description", required = false) String description
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    String email = authentication.getName();

    try {
      rentalService.updateRental(id, name, surface, price, picture, description, email);
      return ResponseEntity.ok(Collections.singletonMap("message", "Rental updated !"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body(e.getMessage());
    } catch (SecurityException e) {
      return ResponseEntity.status(403).body(e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Failed to store picture: " + e.getMessage());
    }
  }
}

