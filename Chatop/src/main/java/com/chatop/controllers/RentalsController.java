package com.chatop.controllers;

import com.chatop.models.Rental;

import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;
import com.chatop.dtos.RentalDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.chatop.models.User;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/api/rentals")
public class RentalsController {

  private final RentalRepository rentalRepository;
  private final UserRepository userRepository;
  private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

  public RentalsController(RentalRepository rentalRepository, UserRepository userRepository) {
    this.rentalRepository = rentalRepository;
    this.userRepository = userRepository;
  }

  @GetMapping
  public ResponseEntity<Map<String, Object>> getAllRentals() {
    List<Rental> rentals = rentalRepository.findAll();

    List<RentalDto> dtos = rentals.stream().map(this::toDto).collect(Collectors.toList());

    return ResponseEntity.ok(Collections.singletonMap("rentals", dtos));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getRentalById(@PathVariable("id") Long id) {
    Optional<Rental> opt = rentalRepository.findById(id);
    if (opt.isEmpty()) {
      return ResponseEntity.status(404).body("Rental not found");
    }
    return ResponseEntity.ok(toDto(opt.get()));
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> createRental(
    @RequestParam("name") String name,
    @RequestParam("surface") Double surface,
    @RequestParam("price") Double price,
    @RequestParam(value = "picture", required = false) MultipartFile picture,
    @RequestParam(value = "description", required = false) String description
  ) {
    // Récupérer l'utilisateur authentifié depuis le SecurityContext
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    String email = authentication.getName();
    User owner = userRepository.findByEmail(email);
    if (owner == null) {
      return ResponseEntity.status(401).body("User not found");
    }

    Rental r = new Rental();
    r.setName(name);
    r.setSurface(surface != null ? BigDecimal.valueOf(surface) : null);
    r.setPrice(price != null ? BigDecimal.valueOf(price) : null);
    r.setDescription(description);
    r.setOwnerId(owner.getId());

    // handle picture saving to static/images/ if provided
    if (picture != null && !picture.isEmpty()) {
      try {
        String imagesDir = "C:\\Users\\david\\Desktop\\OCR\\Projet 3\\Developpez-le-back-end-en-utilisant-Java-et-Spring\\Chatop\\src\\main\\resources\\static\\images";
        Path imagesPath = Path.of(imagesDir).toAbsolutePath().normalize();
        if (!Files.exists(imagesPath)) {
          Files.createDirectories(imagesPath);
        }
        String ext = "";
        String original = picture.getOriginalFilename();
        if (original != null && original.contains(".")) {
          ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = imagesPath.resolve(filename);
        Files.copy(picture.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Stocker uniquement le nom du fichier
        r.setPicture(filename);
      } catch (IOException e) {
        return ResponseEntity.status(500).body("Failed to store picture: " + e.getMessage());
      }
    }

    Rental saved = rentalRepository.save(r);
    return ResponseEntity.ok(Collections.singletonMap("rental", toDto(saved)));
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
    // Récupérer l'utilisateur authentifié depuis le SecurityContext
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(401).body("Unauthorized");
    }

    String email = authentication.getName();
    User currentUser = userRepository.findByEmail(email);
    if (currentUser == null) {
      return ResponseEntity.status(401).body("User not found");
    }

    // Vérifier que le rental existe
    Optional<Rental> optRental = rentalRepository.findById(id);
    if (optRental.isEmpty()) {
      return ResponseEntity.status(404).body("Rental not found");
    }

    Rental rental = optRental.get();

    // Vérifier que l'utilisateur est le propriétaire du rental
    if (!rental.getOwnerId().equals(currentUser.getId())) {
      return ResponseEntity.status(403).body("You are not authorized to update this rental");
    }

    // Mettre à jour les champs
    rental.setName(name);
    rental.setSurface(surface != null ? BigDecimal.valueOf(surface) : null);
    rental.setPrice(price != null ? BigDecimal.valueOf(price) : null);
    rental.setDescription(description);

    // Gérer l'upload de l'image si fournie
    if (picture != null && !picture.isEmpty()) {
      try {
        String imagesDir = "C:\\Users\\david\\Desktop\\OCR\\Projet 3\\Developpez-le-back-end-en-utilisant-Java-et-Spring\\Chatop\\src\\main\\resources\\static\\images";
        Path imagesPath = Path.of(imagesDir).toAbsolutePath().normalize();
        if (!Files.exists(imagesPath)) {
          Files.createDirectories(imagesPath);
        }
        String ext = "";
        String original = picture.getOriginalFilename();
        if (original != null && original.contains(".")) {
          ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = imagesPath.resolve(filename);
        Files.copy(picture.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Stocker uniquement le nom du fichier
        rental.setPicture(filename);
      } catch (IOException e) {
        return ResponseEntity.status(500).body("Failed to store picture: " + e.getMessage());
      }
    }

    rentalRepository.save(rental);
    return ResponseEntity.ok(Collections.singletonMap("message", "Rental updated !"));
  }


  private RentalDto toDto(Rental r) {
    RentalDto dto = new RentalDto();
    dto.setId(r.getId());
    dto.setName(r.getName());
    dto.setSurface(r.getSurface() != null ? r.getSurface().doubleValue() : null);
    dto.setPrice(r.getPrice() != null ? r.getPrice().doubleValue() : null);

    // Construire l'URL complète de l'image pour le front-end
    if (r.getPicture() != null && !r.getPicture().isEmpty()) {
      dto.setPicture("http://localhost:8080/images/" + r.getPicture());
    } else {
      dto.setPicture(r.getPicture());
    }

    dto.setDescription(r.getDescription());
    dto.setOwnerId(r.getOwnerId());
    dto.setCreatedAt(r.getCreatedAt() != null ? r.getCreatedAt().format(DATE_FORMAT) : null);
    dto.setUpdatedAt(r.getUpdatedAt() != null ? r.getUpdatedAt().format(DATE_FORMAT) : null);
    return dto;
  }
}
