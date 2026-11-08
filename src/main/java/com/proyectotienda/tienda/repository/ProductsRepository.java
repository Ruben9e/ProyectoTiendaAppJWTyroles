package com.proyectotienda.tienda.repository;

import com.proyectotienda.tienda.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ProductsRepository extends JpaRepository<Products, Integer> {
    List<Products> findByCategoryId(Integer categoryId);
    Page<Products> findByCategoryId(Integer categoryId, Pageable pageable);
    List<Products> findBySellerId(Integer sellerId);
    Page<Products> findAll(Pageable pageable);
    boolean existsByCategoryId(Integer categoryId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Products p where p.id = :id")
    Optional<Products> findByIdForUpdate(@Param("id") Integer id);
}
