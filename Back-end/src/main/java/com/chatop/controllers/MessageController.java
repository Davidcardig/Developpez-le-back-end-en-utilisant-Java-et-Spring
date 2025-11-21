package com.chatop.controllers;

import com.chatop.dtos.MessageRequest;
import com.chatop.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "API de gestion des messages entre utilisateurs et propriétaires")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(
        summary = "Envoyer un message",
        description = "Permet à un utilisateur authentifié d'envoyer un message concernant une location",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Message envoyé avec succès",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Non autorisé",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Utilisateur ou location non trouvé(e)",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<?> createMessage(@RequestBody MessageRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = authentication.getName();

        try {
            messageService.createMessage(request, email);
            return ResponseEntity.ok(Collections.singletonMap("message", "Message send with success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}

