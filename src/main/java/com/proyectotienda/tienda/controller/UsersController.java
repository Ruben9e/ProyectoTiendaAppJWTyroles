package com.proyectotienda.tienda.controller;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.dto.HttpGlobalResponseDTO;
import com.proyectotienda.tienda.enums.RoleEnum;
import com.proyectotienda.tienda.security.RequiresRole;
import com.proyectotienda.tienda.service.AuthService;
import com.proyectotienda.tienda.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService users;
    private final AuthService auth;
    public UsersController(UsersService users, AuthService auth) { this.users = users; this.auth = auth; }

    @GetMapping("/me")
    @RequiresRole({RoleEnum.ADMIN, RoleEnum.CLIENTE, RoleEnum.VENDEDOR})
    public HttpGlobalResponseDTO<ApiDtos.UserResponse> me(HttpServletRequest request) {
        return new HttpGlobalResponseDTO<>("Perfil del usuario", users.getById((Integer) request.getAttribute("userId")));
    }
    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public HttpGlobalResponseDTO<List<ApiDtos.UserResponse>> all() {
        return new HttpGlobalResponseDTO<>("Usuarios", users.getAll());
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresRole(RoleEnum.ADMIN)
    public HttpGlobalResponseDTO<ApiDtos.UserResponse> create(@Valid @RequestBody ApiDtos.UserCreateRequest request) {
        return new HttpGlobalResponseDTO<>("Usuario creado", auth.createUser(request));
    }
    @PatchMapping("/{id}/role")
    @RequiresRole(RoleEnum.ADMIN)
    public HttpGlobalResponseDTO<ApiDtos.UserResponse> changeRole(@PathVariable int id, @Valid @RequestBody ApiDtos.RoleRequest request) {
        return new HttpGlobalResponseDTO<>("Rol actualizado", users.changeRole(id, request.role()));
    }
    @PatchMapping("/{id}/status")
    @RequiresRole(RoleEnum.ADMIN)
    public HttpGlobalResponseDTO<ApiDtos.UserResponse> setStatus(@PathVariable int id, @Valid @RequestBody ApiDtos.StatusRequest request) {
        return new HttpGlobalResponseDTO<>("Estado actualizado", users.setActive(id, request.active()));
    }
}
