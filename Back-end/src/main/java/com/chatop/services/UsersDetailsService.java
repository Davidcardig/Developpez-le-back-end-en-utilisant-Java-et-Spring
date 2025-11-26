package com.chatop.services;


import com.chatop.models.User;
import com.chatop.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UsersDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // essayer par email
        User user = userRepository.findByEmail(username);

        // Tous les utilisateurs ont le rôle ROLE_USER par défaut
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                // utiliser l'email comme username principal pour la cohérence avec JwtUtils
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

}
