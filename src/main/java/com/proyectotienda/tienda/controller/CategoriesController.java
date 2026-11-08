package com.proyectotienda.tienda.controller;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.dto.HttpGlobalResponseDTO;
import com.proyectotienda.tienda.enums.RoleEnum;
import com.proyectotienda.tienda.security.RequiresRole;
import com.proyectotienda.tienda.service.CategoriesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {
    private final CategoriesService categories;
    public CategoriesController(CategoriesService categories) { this.categories = categories; }
    @GetMapping
    @RequiresRole({RoleEnum.ADMIN, RoleEnum.CLIENTE, RoleEnum.VENDEDOR})
    public HttpGlobalResponseDTO<List<ApiDtos.CategoryResponse>> all() {
        return new HttpGlobalResponseDTO<>("Categorías", categories.getAll());
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresRole(RoleEnum.ADMIN)
    public HttpGlobalResponseDTO<ApiDtos.CategoryResponse> create(@Valid @RequestBody ApiDtos.CategoryRequest request) {
        return new HttpGlobalResponseDTO<>("Categoría creada", categories.create(request.name()));
    }
    @DeleteMapping("/{id}")
    @RequiresRole(RoleEnum.ADMIN)
    public HttpGlobalResponseDTO<Void> delete(@PathVariable int id) {
        categories.delete(id);
        return new HttpGlobalResponseDTO<>("Categoría eliminada", null);
    }
}
