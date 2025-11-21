package com.chatop.services;

import com.chatop.dtos.MessageDto;
import com.chatop.dtos.MessageRequest;
import com.chatop.models.Message;
import com.chatop.models.User;
import com.chatop.repositories.MessageRepository;
import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public MessageService(MessageRepository messageRepository, UserRepository userRepository,
                          RentalRepository rentalRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public void createMessage(MessageRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!rentalRepository.existsById(request.getRental_id())) {
            throw new IllegalArgumentException("Rental not found");
        }

        Message message = new Message();
        message.setMessage(request.getMessage());
        message.setUserId(user.getId());
        message.setRentalId(request.getRental_id());

        messageRepository.save(message);
    }

    private MessageDto toDto(Message message) {
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

