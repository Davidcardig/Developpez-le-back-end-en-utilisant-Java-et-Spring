package com.chatop.mappers;

import com.chatop.dtos.MessageRequest;
import com.chatop.models.Message;

public class MessageMapper {

    public static Message toEntity(MessageRequest dto, Integer userId) {
        if (dto == null) return null;

        Message message = new Message();
        message.setMessage(dto.getMessage());
        message.setUserId(userId);
        message.setRentalId(dto.getRental_id());

        return message;
    }
}

