package com.chatop.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO pour créer ou mettre à jour une location")
public class RentalRequestDto {

    @Schema(description = "Nom de la location", required = true, example = "Appartement Paris")
    private String name;

    @Schema(description = "Surface en m²", required = true, example = "75.5")
    private Double surface;

    @Schema(description = "Prix de location", required = true, example = "1200.0")
    private Double price;

    @Schema(description = "Photo de la location", type = "string", format = "binary")
    private MultipartFile picture;

    @Schema(description = "Description détaillée de la location", example = "Belle appartement avec vue sur la Tour Eiffel")
    private String description;
}

