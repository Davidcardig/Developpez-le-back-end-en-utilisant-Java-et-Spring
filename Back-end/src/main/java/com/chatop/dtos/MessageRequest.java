package com.chatop.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String message;
    private Integer rental_id;
    private Integer user_id;
}

