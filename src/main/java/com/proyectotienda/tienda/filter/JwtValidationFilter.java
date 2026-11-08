package com.proyectotienda.tienda.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyectotienda.tienda.dto.HttpGlobalResponseDTO;
import com.proyectotienda.tienda.repository.UsersRepository;
import com.proyectotienda.tienda.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Map;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final ObjectMapper objectMapper;

    public JwtValidationFilter(JwtService jwtService, UsersRepository usersRepository, ObjectMapper objectMapper) {
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path.startsWith("/auth/")) return true;
        return "GET".equalsIgnoreCase(request.getMethod())
                && ("/products".equals(path) || path.matches("/products/\\d+"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() <= 7) {
            writeUnauthorized(response, "Se requiere un token Bearer válido");
            return;
        }
        try {
            String token = authorization.substring(7).trim();
            Map<String, Object> claims = jwtService.validateAndRead(token);
            Integer userId = ((Number) claims.get("userId")).intValue();
            Integer roleId = ((Number) claims.get("rolId")).intValue();
            String username = (String) claims.get("sub");
            if (username == null || usersRepository.findById(userId).filter(user -> user.isActive()
                    && user.getUsername().equals(username) && user.getRole().getId().equals(roleId)).isEmpty()) {
                writeUnauthorized(response, "Token inválido o usuario inactivo");
                return;
            }
            request.setAttribute("username", username);
            request.setAttribute("userId", userId);
            request.setAttribute("rolId", roleId);
            chain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException | ClassCastException ex) {
            writeUnauthorized(response, "Token inválido o expirado");
        }
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new HttpGlobalResponseDTO<>(message, null));
    }
}
