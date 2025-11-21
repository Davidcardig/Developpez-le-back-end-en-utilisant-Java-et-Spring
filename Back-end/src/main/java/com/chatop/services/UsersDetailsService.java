package com.chatop.services;


import com.chatop.models.User;
import com.chatop.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        String rolesRaw = user.getRole();
        if (rolesRaw == null || rolesRaw.trim().isEmpty()) {
            rolesRaw = "ROLE_USER";
        }

        List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesRaw.split(","))
                .map(String::trim)
                .filter(r -> !r.isEmpty())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                // utiliser l'email comme username principal pour la cohérence avec JwtUtils
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

}
