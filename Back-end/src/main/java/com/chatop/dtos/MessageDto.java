package com.chatop.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private Long id;
    private Long rentalId;
    private Long userId;
    private String message;
    private String createdAt;
    private String updatedAt;
}

