package com.chatop.services;

import com.chatop.dtos.MessageRequest;
import com.chatop.mappers.MessageMapper;
import com.chatop.models.Message;
import com.chatop.models.User;
import com.chatop.repositories.MessageRepository;
import com.chatop.repositories.RentalRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RentalRepository rentalRepository;
    private final CurrentUserService currentUserService;

    public MessageService(MessageRepository messageRepository,
                          RentalRepository rentalRepository,
                          CurrentUserService currentUserService) {
        this.messageRepository = messageRepository;
        this.rentalRepository = rentalRepository;
        this.currentUserService = currentUserService;
    }

    public void createMessage(MessageRequest request) {
        User user = currentUserService.getCurrentUser();

        if (!rentalRepository.existsById(request.getRental_id())) {
            throw new IllegalArgumentException("Rental not found");
        }

        Message message = MessageMapper.toEntity(request, user.getId());
        messageRepository.save(message);
    }
}

