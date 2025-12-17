package com.chatop.mappers;

import com.chatop.dtos.MessageDto;
import com.chatop.dtos.MessageRequest;
import com.chatop.models.Message;

import java.time.format.DateTimeFormatter;

public class MessageMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public static Message toEntity(MessageRequest dto, Integer userId) {
        if (dto == null) return null;

        Message message = new Message();
        message.setMessage(dto.getMessage());
        message.setUserId(userId);
        message.setRentalId(dto.getRental_id());

        return message;
    }

    public static MessageDto toDto(Message message) {
        if (message == null) return null;

        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setRentalId(message.getRentalId());
        dto.setUserId(message.getUserId());
        dto.setMessage(message.getMessage());
        dto.setCreatedAt(message.getCreatedAt() != null ? message.getCreatedAt().format(DATE_FORMAT) : null);
        dto.setUpdatedAt(message.getUpdatedAt() != null ? message.getUpdatedAt().format(DATE_FORMAT) : null);

        return dto;
    }
}

