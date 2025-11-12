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
import com.chatop.configuration.JwtUtils;
import com.chatop.models.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
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
    private final JwtUtils jwtUtils;

    public RentalsController(RentalRepository rentalRepository, UserRepository userRepository, JwtUtils jwtUtils) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
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
        return ResponseEntity.ok(Collections.singletonMap("rental", toDto(opt.get())));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRental(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            @RequestParam(value = "description", required = false) String description
    ) {


        String token;
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7).trim();
        } else {
            token = authorization.trim();
        }

        String email;
            email = jwtUtils.extractName(token);
        User owner = userRepository.findByEmail(email);


        Rental r = new Rental();
        r.setName(name);
        r.setSurface(surface != null ? BigDecimal.valueOf(surface) : null);
        r.setPrice(price != null ? BigDecimal.valueOf(price) : null);
        r.setDescription(description);
        r.setOwnerId(owner.getId());


        if (picture != null && !picture.isEmpty()) {
            try {
                String uploadsDir = System.getProperty("user.dir") + "\\..\\..\\uploads"; // repo root uploads folder
                Path uploadsPath = Path.of(uploadsDir).toAbsolutePath().normalize();
                if (!Files.exists(uploadsPath)) {
                    Files.createDirectories(uploadsPath);
                }
                String ext = "";
                String original = picture.getOriginalFilename();
                if (original != null && original.contains(".")) {
                    ext = original.substring(original.lastIndexOf('.'));
                }
                String filename = UUID.randomUUID() + ext;
                Path target = uploadsPath.resolve(filename);
                Files.copy(picture.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                r.setPicture(filename);
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Failed to store picture: " + e.getMessage());
            }
        }

        Rental saved = rentalRepository.save(r);
        return ResponseEntity.ok(Collections.singletonMap("rental", toDto(saved)));
    }

    private RentalDto toDto(Rental r) {
        RentalDto dto = new RentalDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setSurface(r.getSurface() != null ? r.getSurface().doubleValue() : null);
        dto.setPrice(r.getPrice() != null ? r.getPrice().doubleValue() : null);
        dto.setPicture(r.getPicture());
        dto.setDescription(r.getDescription());
        dto.setOwnerId(r.getOwnerId());
        dto.setCreatedAt(r.getCreatedAt() != null ? r.getCreatedAt().format(DATE_FORMAT) : null);
        dto.setUpdatedAt(r.getUpdatedAt() != null ? r.getUpdatedAt().format(DATE_FORMAT) : null);
        return dto;
    }
}
