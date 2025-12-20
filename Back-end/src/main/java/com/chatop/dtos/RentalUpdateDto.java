package com.chatop.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalUpdateDto {
    private String name;
    private Double surface;
    private Double price;
    private String description;
}

