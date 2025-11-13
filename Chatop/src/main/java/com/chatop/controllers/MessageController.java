package com.chatop.controllers;

import com.chatop.dtos.MessageDto;
import com.chatop.dtos.MessageRequest;
import com.chatop.models.Message;
import com.chatop.models.User;
import com.chatop.repositories.MessageRepository;
import com.chatop.repositories.RentalRepository;
import com.chatop.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    public MessageController(MessageRepository messageRepository, UserRepository userRepository, RentalRepository rentalRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody MessageRequest request) {
        // Récupérer l'utilisateur authentifié depuis le SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(401).body("User not found");
        }

        // Vérifier que le rental existe
        if (!rentalRepository.existsById(request.getRental_id())) {
            return ResponseEntity.status(404).body("Rental not found");
        }

        // Créer le message
        Message message = new Message();
        message.setMessage(request.getMessage());
        message.setUserId(user.getId()); // On utilise toujours l'utilisateur authentifié, pas celui du request
        message.setRentalId(request.getRental_id());

        messageRepository.save(message);

        return ResponseEntity.ok(Collections.singletonMap("message", "Message send with success"));
    }

    private MessageDto toDto(Message m) {
        MessageDto dto = new MessageDto();
        dto.setId(m.getId());
        dto.setRentalId(m.getRentalId());
        dto.setUserId(m.getUserId());
        dto.setMessage(m.getMessage());
        dto.setCreatedAt(m.getCreatedAt() != null ? m.getCreatedAt().format(DATE_FORMAT) : null);
        dto.setUpdatedAt(m.getUpdatedAt() != null ? m.getUpdatedAt().format(DATE_FORMAT) : null);
        return dto;
    }
}

