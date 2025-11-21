package com.chatop.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String message;
    private Long rental_id;
    private Long user_id;
}

