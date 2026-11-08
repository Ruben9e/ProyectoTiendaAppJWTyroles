package com.proyectotienda.tienda.controller;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.dto.HttpGlobalResponseDTO;
import com.proyectotienda.tienda.enums.RoleEnum;
import com.proyectotienda.tienda.security.RequiresRole;
import com.proyectotienda.tienda.service.ProductsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
@Validated
public class ProductsController {
    private final ProductsService products;
    public ProductsController(ProductsService products) { this.products = products; }
    @GetMapping
    public HttpGlobalResponseDTO<ApiDtos.ProductPageResponse> all(
            @RequestParam(required = false) @Positive Integer categoryId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return new HttpGlobalResponseDTO<>("Catálogo de productos", products.getAll(categoryId, page, size));
    }
    @GetMapping("/{id}")
    public HttpGlobalResponseDTO<ApiDtos.ProductResponse> one(@PathVariable int id) {
        return new HttpGlobalResponseDTO<>("Producto", products.getById(id));
    }
    @GetMapping("/mine")
    @RequiresRole(RoleEnum.VENDEDOR)
    public HttpGlobalResponseDTO<List<ApiDtos.ProductResponse>> mine(HttpServletRequest request) {
        return new HttpGlobalResponseDTO<>("Mis productos", products.getMine((Integer) request.getAttribute("userId")));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresRole(RoleEnum.VENDEDOR)
    public HttpGlobalResponseDTO<ApiDtos.ProductResponse> create(@Valid @RequestBody ApiDtos.ProductRequest request,
                                                                  HttpServletRequest servletRequest) {
        return new HttpGlobalResponseDTO<>("Producto creado",
                products.create(request, (Integer) servletRequest.getAttribute("userId")));
    }
    @PutMapping("/{id}")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public HttpGlobalResponseDTO<ApiDtos.ProductResponse> update(@PathVariable int id,
            @Valid @RequestBody ApiDtos.ProductRequest request, HttpServletRequest servletRequest) {
        return new HttpGlobalResponseDTO<>("Producto actualizado", products.update(id, request,
                (Integer) servletRequest.getAttribute("userId"), (Integer) servletRequest.getAttribute("rolId")));
    }
    @DeleteMapping("/{id}")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public HttpGlobalResponseDTO<Void> delete(@PathVariable int id, HttpServletRequest request) {
        products.delete(id, (Integer) request.getAttribute("userId"), (Integer) request.getAttribute("rolId"));
        return new HttpGlobalResponseDTO<>("Producto eliminado", null);
    }
}
