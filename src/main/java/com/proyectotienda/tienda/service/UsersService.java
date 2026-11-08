package com.proyectotienda.tienda.service;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.entity.Users;
import com.proyectotienda.tienda.enums.RoleEnum;
import com.proyectotienda.tienda.repository.RolesRepository;
import com.proyectotienda.tienda.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UsersService {
    private final UsersRepository users;
    private final RolesRepository roles;
    public UsersService(UsersRepository users, RolesRepository roles) { this.users = users; this.roles = roles; }

    public ApiDtos.UserResponse getById(int id) { return AuthService.toResponse(findUser(id)); }
    public List<ApiDtos.UserResponse> getAll() { return users.findAll().stream().map(AuthService::toResponse).toList(); }

    @Transactional
    public ApiDtos.UserResponse changeRole(int id, RoleEnum roleName) {
        Users user = findUser(id);
        user.setRole(roles.findByName(roleName)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Rol no válido")));
        return AuthService.toResponse(user);
    }

    @Transactional
    public ApiDtos.UserResponse setActive(int id, boolean active) {
        Users user = findUser(id);
        user.setActive(active);
        return AuthService.toResponse(user);
    }

    private Users findUser(int id) {
        return users.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
