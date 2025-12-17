package com.chatop.services;

import com.chatop.models.User;
import com.chatop.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service centralisé pour récupérer l'utilisateur connecté
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Récupère l'utilisateur actuellement connecté depuis le contexte de sécurité
     *
     * @return l'utilisateur connecté
     * @throws IllegalArgumentException si l'utilisateur n'est pas trouvé
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }

    /**
     * Récupère l'email de l'utilisateur actuellement connecté
     *
     * @return l'email de l'utilisateur connecté
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

