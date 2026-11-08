package com.proyectotienda.tienda.controller;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.dto.HttpGlobalResponseDTO;
import com.proyectotienda.tienda.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<HttpGlobalResponseDTO<ApiDtos.UserResponse>> register(@Valid @RequestBody ApiDtos.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new HttpGlobalResponseDTO<>("Usuario registrado", authService.register(request)));
    }

    @PostMapping("/login")
    public HttpGlobalResponseDTO<ApiDtos.AuthResponse> login(@Valid @RequestBody ApiDtos.LoginRequest request) {
        return new HttpGlobalResponseDTO<>("Inicio de sesión correcto", authService.login(request));
    }

    @PostMapping("/refreshtoken")
    public HttpGlobalResponseDTO<ApiDtos.AuthResponse> refresh(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length() <= 7) {
            throw new com.proyectotienda.tienda.service.ApiException(HttpStatus.UNAUTHORIZED, "Se requiere un token Bearer válido");
        }
        return new HttpGlobalResponseDTO<>("Token renovado", authService.refresh(authorization.substring(7).trim()));
    }
}
