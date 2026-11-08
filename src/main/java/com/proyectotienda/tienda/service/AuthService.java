package com.proyectotienda.tienda.service;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.entity.Roles;
import com.proyectotienda.tienda.entity.Users;
import com.proyectotienda.tienda.enums.RoleEnum;
import com.proyectotienda.tienda.repository.RolesRepository;
import com.proyectotienda.tienda.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UsersRepository users;
    private final RolesRepository roles;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UsersRepository users, RolesRepository roles, BCryptPasswordEncoder encoder, JwtService jwtService) {
        this.users = users; this.roles = roles; this.encoder = encoder; this.jwtService = jwtService;
    }

    @Transactional
    public ApiDtos.UserResponse register(ApiDtos.RegisterRequest request) {
        ensureUnique(request.username(), request.email());
        Roles role = roles.findByName(RoleEnum.CLIENTE)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "No existe el rol CLIENTE"));
        return toResponse(users.save(new Users(request.username(), request.email(),
                encoder.encode(request.password()), role)));
    }

    public ApiDtos.AuthResponse login(ApiDtos.LoginRequest request) {
        Users user = users.findByUsernameOrEmail(request.user(), request.user())
                .filter(Users::isActive)
                .filter(found -> encoder.matches(request.password(), found.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas o usuario inactivo"));
        return new ApiDtos.AuthResponse(jwtService.generateToken(user), toResponse(user));
    }

    public ApiDtos.AuthResponse refresh(String token) {
        try {
            var claims = jwtService.validateAndRead(token);
            int userId = ((Number) claims.get("userId")).intValue();
            Users user = users.findById(userId)
                    .filter(Users::isActive)
                    .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuario inactivo o inexistente"));
            if (!user.getUsername().equals(claims.getSubject())) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "Token inválido");
            }
            return new ApiDtos.AuthResponse(jwtService.generateToken(user), toResponse(user));
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException | ClassCastException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }
    }

    @Transactional
    public ApiDtos.UserResponse createUser(ApiDtos.UserCreateRequest request) {
        ensureUnique(request.username(), request.email());
        Roles role = roles.findByName(request.role())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Rol no válido"));
        Users user = new Users(request.username(), request.email(), encoder.encode(request.password()), role);
        return toResponse(users.save(user));
    }

    private void ensureUnique(String username, String email) {
        if (users.existsByUsername(username) || users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "El username o email ya está registrado");
        }
    }

    public static ApiDtos.UserResponse toResponse(Users user) {
        return new ApiDtos.UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.isActive(), user.getRole().getName());
    }
}
