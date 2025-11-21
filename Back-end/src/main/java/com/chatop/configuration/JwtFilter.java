package com.chatop.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;
import com.chatop.services.UsersDetailsService;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    private final UsersDetailsService customUserDetailsService;
    private final JwtUtils jwtUtil;

    public JwtFilter(UsersDetailsService customUserDetailsService, JwtUtils jwtUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }
    @Override

    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null) {
            if (authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            } else {
                // accepter le token seul dans l'en-tête
                jwt = authHeader.trim();
            }
        }

        if (jwt != null) {
            try {
                username = jwtUtil.extractName(jwt);
            } catch (Exception e) {
                // token invalide ou mal formé -> laisser username null
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // charger les détails de l'utilisateur
            try {
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =  new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception ex) {

            }
        }

        // continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }

}
