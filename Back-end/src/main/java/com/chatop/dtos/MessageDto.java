package com.chatop.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private Integer id;
    private Integer rentalId;
    private Integer userId;
    private String message;
    private String createdAt;
    private String updatedAt;
}

