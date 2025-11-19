package com.chatop.services;

import com.chatop.dtos.RentalDto;
import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final String imagesDir = "C:\\Users\\david\\Desktop\\OCR\\Projet 3\\Developpez-le-back-end-en-utilisant-Java-et-Spring\\Chatop\\src\\main\\resources\\static\\images";

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    public List<RentalDto> getAllRentals() {
        List<Rental> rentals = rentalRepository.findAll();
        return rentals.stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<RentalDto> getRentalById(Long id) {
        return rentalRepository.findById(id).map(this::toDto);
    }

    public RentalDto createRental(String name, Double surface, Double price,
                                   MultipartFile picture, String description, String userEmail) throws IOException {
        User owner = userRepository.findByEmail(userEmail);
        if (owner == null) {
            throw new IllegalArgumentException("User not found");
        }

        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface != null ? BigDecimal.valueOf(surface) : null);
        rental.setPrice(price != null ? BigDecimal.valueOf(price) : null);
        rental.setDescription(description);
        rental.setOwnerId(owner.getId());

        if (picture != null && !picture.isEmpty()) {
            String filename = savePicture(picture);
            rental.setPicture(filename);
        }

        Rental saved = rentalRepository.save(rental);
        return toDto(saved);
    }

    public RentalDto updateRental(Long id, String name, Double surface, Double price,
                                   MultipartFile picture, String description, String userEmail) throws IOException {
        User currentUser = userRepository.findByEmail(userEmail);
        if (currentUser == null) {
            throw new IllegalArgumentException("User not found");
        }

        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!rental.getOwnerId().equals(currentUser.getId())) {
            throw new SecurityException("You are not authorized to update this rental");
        }

        rental.setName(name);
        rental.setSurface(surface != null ? BigDecimal.valueOf(surface) : null);
        rental.setPrice(price != null ? BigDecimal.valueOf(price) : null);
        rental.setDescription(description);

        if (picture != null && !picture.isEmpty()) {
            String filename = savePicture(picture);
            rental.setPicture(filename);
        }

        rentalRepository.save(rental);
        return toDto(rental);
    }

    private String savePicture(MultipartFile picture) throws IOException {
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

        return filename;
    }

    private RentalDto toDto(Rental rental) {
        RentalDto dto = new RentalDto();
        dto.setId(rental.getId());
        dto.setName(rental.getName());
        dto.setSurface(rental.getSurface() != null ? rental.getSurface().doubleValue() : null);
        dto.setPrice(rental.getPrice() != null ? rental.getPrice().doubleValue() : null);

        if (rental.getPicture() != null && !rental.getPicture().isEmpty()) {
            dto.setPicture("http://localhost:8080/images/" + rental.getPicture());
        } else {
            dto.setPicture(rental.getPicture());
        }

        dto.setDescription(rental.getDescription());
        dto.setOwnerId(rental.getOwnerId());
        dto.setCreatedAt(rental.getCreatedAt() != null ? rental.getCreatedAt().format(DATE_FORMAT) : null);
        dto.setUpdatedAt(rental.getUpdatedAt() != null ? rental.getUpdatedAt().format(DATE_FORMAT) : null);
        return dto;
    }
}


