package com.chatop.services;

import com.chatop.dtos.RentalDto;
import com.chatop.dtos.RentalRequestDto;
import com.chatop.mappers.RentalMapper;
import com.chatop.models.Rental;
import com.chatop.models.User;
import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    @Value("${app.images.directory:src/main/resources/static/images}")
    private String imagesDir;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    public List<RentalDto> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(RentalMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<RentalDto> getRentalById(Integer id) {
        return rentalRepository.findById(id).map(RentalMapper::toDto);
    }

    public RentalDto createRental(RentalRequestDto rentalRequest) throws IOException {
        User owner = getCurrentUser();
        Rental rental = RentalMapper.toEntity(rentalRequest, owner);

        if (rentalRequest.getPicture() != null && !rentalRequest.getPicture().isEmpty()) {
            rental.setPicture(savePicture(rentalRequest.getPicture()));
        }

        return RentalMapper.toDto(rentalRepository.save(rental));
    }

    public RentalDto updateRental(Integer id, RentalRequestDto rentalRequest) throws IOException {
        User currentUser = getCurrentUser();
        Rental rental = rentalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (rental.getOwner() == null || !rental.getOwner().getId().equals(currentUser.getId())) {
            throw new SecurityException("You are not authorized to update this rental");
        }

        RentalMapper.updateEntity(rental, rentalRequest);

        if (rentalRequest.getPicture() != null && !rentalRequest.getPicture().isEmpty()) {
            rental.setPicture(savePicture(rentalRequest.getPicture()));
        }

        return RentalMapper.toDto(rentalRepository.save(rental));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    private String savePicture(MultipartFile picture) throws IOException {
        // Construit le chemin complet depuis le répertoire de travail
        Path imagesPath = Paths.get(System.getProperty("user.dir")).resolve(imagesDir);

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
}


