package com.proyectotienda.tienda.repository;

import com.proyectotienda.tienda.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    List<Orders> findByClientId(Integer clientId);
    List<Orders> findByProductSellerId(Integer sellerId);
    boolean existsByProductId(Integer productId);
}
