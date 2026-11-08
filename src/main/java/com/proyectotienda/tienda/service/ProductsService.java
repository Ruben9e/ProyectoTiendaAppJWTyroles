package com.proyectotienda.tienda.service;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.entity.Categories;
import com.proyectotienda.tienda.entity.Products;
import com.proyectotienda.tienda.entity.Users;
import com.proyectotienda.tienda.enums.RoleEnum;
import com.proyectotienda.tienda.repository.CategoriesRepository;
import com.proyectotienda.tienda.repository.OrdersRepository;
import com.proyectotienda.tienda.repository.ProductsRepository;
import com.proyectotienda.tienda.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductsService {
    private final ProductsRepository products;
    private final CategoriesRepository categories;
    private final UsersRepository users;
    private final OrdersRepository orders;
    public ProductsService(ProductsRepository products, CategoriesRepository categories,
                           UsersRepository users, OrdersRepository orders) {
        this.products = products; this.categories = categories; this.users = users; this.orders = orders;
    }
    public ApiDtos.ProductPageResponse getAll(Integer categoryId, int page, int size) {
        Page<Products> result = categoryId == null
                ? products.findAll(PageRequest.of(page, size))
                : products.findByCategoryId(categoryId, PageRequest.of(page, size));
        return new ApiDtos.ProductPageResponse(result.getContent().stream().map(ProductsService::toResponse).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
    public ApiDtos.ProductResponse getById(int id) { return toResponse(findProduct(id)); }
    public List<ApiDtos.ProductResponse> getMine(int sellerId) {
        return products.findBySellerId(sellerId).stream().map(ProductsService::toResponse).toList();
    }
    @Transactional
    public ApiDtos.ProductResponse create(ApiDtos.ProductRequest request, int sellerId) {
        Users seller = users.findById(sellerId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Vendedor no encontrado"));
        Categories category = findCategory(request.categoryId());
        return toResponse(products.save(new Products(request.name(), request.description(), request.price(),
                request.stock(), category, seller)));
    }
    @Transactional
    public ApiDtos.ProductResponse update(int id, ApiDtos.ProductRequest request, int actorId, int roleId) {
        Products product = findProduct(id);
        if (roleId != RoleEnum.ADMIN.getId() && !product.getSeller().getId().equals(actorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes modificar un producto ajeno");
        }
        product.update(request.name(), request.description(), request.price(), request.stock(), findCategory(request.categoryId()));
        return toResponse(product);
    }
    @Transactional
    public void delete(int id, int actorId, int roleId) {
        Products product = findProduct(id);
        if (roleId != RoleEnum.ADMIN.getId() && !product.getSeller().getId().equals(actorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes eliminar un producto ajeno");
        }
        if (orders.existsByProductId(id)) throw new ApiException(HttpStatus.CONFLICT, "El producto tiene pedidos asociados");
        products.delete(product);
    }
    public Products findProduct(int id) {
        return products.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }
    public Categories findCategory(int id) {
        return categories.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }
    private static ApiDtos.ProductResponse toResponse(Products product) {
        return new ApiDtos.ProductResponse(product.getId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getStock(), product.getCategory().getId(), product.getCategory().getName(),
                product.getSeller().getId(), product.getSeller().getUsername());
    }
}
