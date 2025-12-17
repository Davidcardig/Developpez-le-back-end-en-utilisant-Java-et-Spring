package com.chatop.services;

import com.chatop.dtos.MessageRequest;
import com.chatop.mappers.MessageMapper;
import com.chatop.models.Message;
import com.chatop.models.User;
import com.chatop.repositories.MessageRepository;
import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository,
                          RentalRepository rentalRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public void createMessage(MessageRequest request) {
        User user = getCurrentUser();

        if (!rentalRepository.existsById(request.getRental_id())) {
            throw new IllegalArgumentException("Rental not found");
        }

        Message message = MessageMapper.toEntity(request, user.getId());
        messageRepository.save(message);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }
}

