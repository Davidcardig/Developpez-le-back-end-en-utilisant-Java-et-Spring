package com.chatop.controllers;

import com.chatop.models.Rental;

import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;
import com.chatop.dtos.RentalDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rentals")
public class RentalsController {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final ObjectMapper objectMapper = new ObjectMapper();

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
