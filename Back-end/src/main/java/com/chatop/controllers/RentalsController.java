package com.chatop.controllers;

import com.chatop.dtos.RentalDto;
import com.chatop.dtos.RentalRequestDto;
import com.chatop.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
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
  @GetMapping // GET /api/rentals
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
  @GetMapping("/{id}") // GET /api/rentals/{id}
  public ResponseEntity<?> getRentalById(
    @Parameter(description = "ID de la location", required = true)
    @PathVariable("id") Integer id) {
    Optional<RentalDto> rental = rentalService.getRentalById(id);
    // Lance IllegalArgumentException si non trouvé, gérée par GlobalExceptionHandler
    return ResponseEntity.ok(rental.orElseThrow(() ->
        new IllegalArgumentException("Rental not found with id: " + id)));
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
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // POST /api/rentals
  public ResponseEntity<?> createRental(
    @RequestBody(description = "Données de la nouvelle location", required = true,
                 content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                    schema = @Schema(implementation = RentalRequestDto.class)))
    @ModelAttribute RentalRequestDto rentalRequest
  ) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    // Les exceptions (IOException, IllegalArgumentException) seront gérées par GlobalExceptionHandler
    rentalService.createRental(
      rentalRequest.getName(),
      rentalRequest.getSurface(),
      rentalRequest.getPrice(),
      rentalRequest.getPicture(),
      rentalRequest.getDescription(),
      email
    );
    return ResponseEntity.ok(Collections.singletonMap("message", "Rental created !"));
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

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // PUT /api/rentals/{id}
  public ResponseEntity<?> updateRental(
    @Parameter(description = "ID de la location à modifier", required = true)
    @PathVariable("id") Integer id,
    @RequestBody(description = "Nouvelles données de la location", required = true,
                 content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                    schema = @Schema(implementation = RentalRequestDto.class)))
    @ModelAttribute RentalRequestDto rentalRequest
  ) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    // Les exceptions (IOException, SecurityException, IllegalArgumentException) seront gérées par GlobalExceptionHandler
    rentalService.updateRental(
      id,
      rentalRequest.getName(),
      rentalRequest.getSurface(),
      rentalRequest.getPrice(),
      rentalRequest.getPicture(),
      rentalRequest.getDescription(),
      email
    );
    return ResponseEntity.ok(Collections.singletonMap("message", "Rental updated !"));
  }
}

