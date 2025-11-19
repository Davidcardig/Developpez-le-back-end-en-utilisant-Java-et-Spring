package com.chatop.controllers;

import com.chatop.dtos.RentalDto;
import com.chatop.services.RentalService;
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
public class RentalsController {

  private final RentalService rentalService;

  public RentalsController(RentalService rentalService) {
    this.rentalService = rentalService;
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> getAllRentals() {
    List<RentalDto> rentals = rentalService.getAllRentals();
    return ResponseEntity.ok(Collections.singletonMap("rentals", rentals));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getRentalById(@PathVariable("id") Long id) {
    Optional<RentalDto> rental = rentalService.getRentalById(id);
    if (rental.isEmpty()) {
      return ResponseEntity.status(404).body("Rental not found");
    }
    return ResponseEntity.ok(rental.get());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createRental(
    @RequestParam("name") String name,
    @RequestParam("surface") Double surface,
    @RequestParam("price") Double price,
    @RequestParam(value = "picture", required = false) MultipartFile picture,
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


  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> updateRental(
    @PathVariable("id") Long id,
    @RequestParam("name") String name,
    @RequestParam("surface") Double surface,
    @RequestParam("price") Double price,
    @RequestParam(value = "picture", required = false) MultipartFile picture,
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

