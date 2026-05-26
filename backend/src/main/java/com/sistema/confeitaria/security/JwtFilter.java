package com.sistema.confeitaria.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");

        // Log de entrada para monitoramento no Docker
        if (request.getRequestURI().startsWith("/api/admin")) {
            System.out.println("[DOCKER-SECURITY] Tentativa de acesso à rota protegida: " + request.getRequestURI());
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                
                // Alterado de Collections.emptyList() para conter a autoridade de ADMIN
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username, 
                        null, 
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("[DOCKER-SECURITY] Token validado com sucesso para o usuário: " + username);
            } else {
                System.out.println("[DOCKER-SECURITY] Token recebido, mas foi rejeitado pelo JwtUtil.");
            }
        }
        
        filterChain.doFilter(request, response);
    }
}