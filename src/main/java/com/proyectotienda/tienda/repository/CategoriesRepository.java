package com.proyectotienda.tienda.repository;

import com.proyectotienda.tienda.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Categories> findByNameIgnoreCase(String name);
}
