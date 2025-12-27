package com.chatop.mappers;

import com.chatop.dtos.RentalDto;
import com.chatop.dtos.RentalRequestDto;
import com.chatop.dtos.RentalUpdateDto;
import com.chatop.models.Rental;
import com.chatop.models.User;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class RentalMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public static Rental toEntity(RentalRequestDto dto, User owner) {
        if (dto == null) return null;
        Rental rental = new Rental();
        rental.setName(dto.getName());
        rental.setSurface(dto.getSurface() != null ? BigDecimal.valueOf(dto.getSurface())
                : null);
        rental.setPrice(dto.getPrice() != null ? BigDecimal.valueOf(dto.getPrice())
                : null);
        rental.setDescription(dto.getDescription());
        rental.setOwner(owner);
        return rental;
    }

    public static void updateEntityFromUpdateDto(Rental rental, RentalUpdateDto dto) {
        if (rental == null || dto == null) return;

        rental.setName(dto.getName());
        rental.setSurface(dto.getSurface() != null ? BigDecimal.valueOf(dto.getSurface()) : null);
        rental.setPrice(dto.getPrice() != null ? BigDecimal.valueOf(dto.getPrice()) : null);
        rental.setDescription(dto.getDescription());
    }

    public static RentalDto toDto(Rental rental) {
        if (rental == null) return null;

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
        dto.setOwnerId(rental.getOwner() != null ? rental.getOwner().getId() : null);
        dto.setCreatedAt(rental.getCreatedAt() != null ? rental.getCreatedAt().format(DATE_FORMAT) : null);
        dto.setUpdatedAt(rental.getUpdatedAt() != null ? rental.getUpdatedAt().format(DATE_FORMAT) : null);

        return dto;
    }
}

