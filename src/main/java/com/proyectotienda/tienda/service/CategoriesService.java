package com.proyectotienda.tienda.service;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.entity.Categories;
import com.proyectotienda.tienda.repository.CategoriesRepository;
import com.proyectotienda.tienda.repository.ProductsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoriesService {
    private final CategoriesRepository categories;
    private final ProductsRepository products;
    public CategoriesService(CategoriesRepository categories, ProductsRepository products) {
        this.categories = categories; this.products = products;
    }
    public List<ApiDtos.CategoryResponse> getAll() { return categories.findAll().stream().map(CategoriesService::toResponse).toList(); }
    @Transactional
    public ApiDtos.CategoryResponse create(String name) {
        if (categories.existsByNameIgnoreCase(name)) throw new ApiException(HttpStatus.CONFLICT, "La categoría ya existe");
        return toResponse(categories.save(new Categories(name)));
    }
    @Transactional
    public void delete(int id) {
        Categories category = categories.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
        if (products.existsByCategoryId(id)) throw new ApiException(HttpStatus.CONFLICT, "La categoría tiene productos asociados");
        categories.delete(category);
    }
    private static ApiDtos.CategoryResponse toResponse(Categories category) {
        return new ApiDtos.CategoryResponse(category.getId(), category.getName());
    }
}
