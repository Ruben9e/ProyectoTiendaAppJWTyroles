package com.proyectotienda.tienda.service;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.entity.Orders;
import com.proyectotienda.tienda.entity.Products;
import com.proyectotienda.tienda.entity.Users;
import com.proyectotienda.tienda.enums.OrderStatusEnum;
import com.proyectotienda.tienda.enums.RoleEnum;
import com.proyectotienda.tienda.repository.OrdersRepository;
import com.proyectotienda.tienda.repository.ProductsRepository;
import com.proyectotienda.tienda.repository.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrdersService {
    private final OrdersRepository orders;
    private final ProductsRepository products;
    private final UsersRepository users;
    public OrdersService(OrdersRepository orders, ProductsRepository products, UsersRepository users) {
        this.orders = orders; this.products = products; this.users = users;
    }
    @Transactional
    public ApiDtos.OrderResponse create(ApiDtos.OrderRequest request, int clientId) {
        Users client = users.findById(clientId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        Products product = products.findByIdForUpdate(request.productId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        if (product.getStock() < request.quantity()) throw new ApiException(HttpStatus.BAD_REQUEST, "Stock insuficiente");
        product.reduceStock(request.quantity());
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(request.quantity()));
        return toResponse(orders.save(new Orders(client, product, request.quantity(), total)));
    }
    public List<ApiDtos.OrderResponse> getMine(int clientId) { return orders.findByClientId(clientId).stream().map(OrdersService::toResponse).toList(); }
    public List<ApiDtos.OrderResponse> getSales(int sellerId) { return orders.findByProductSellerId(sellerId).stream().map(OrdersService::toResponse).toList(); }
    public List<ApiDtos.OrderResponse> getAll() { return orders.findAll().stream().map(OrdersService::toResponse).toList(); }
    @Transactional
    public ApiDtos.OrderResponse updateStatus(int id, OrderStatusEnum status, int actorId, int roleId) {
        Orders order = findOrder(id);
        if (roleId == RoleEnum.VENDEDOR.getId() && !order.getProduct().getSeller().getId().equals(actorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes modificar un pedido de otro vendedor");
        }
        if (order.getStatus() == OrderStatusEnum.CANCELADO && status != OrderStatusEnum.CANCELADO) {
            throw new ApiException(HttpStatus.CONFLICT, "Un pedido cancelado no se puede reactivar");
        }
        if (status == OrderStatusEnum.CANCELADO && order.getStatus() != OrderStatusEnum.CANCELADO) {
            order.getProduct().addStock(order.getQuantity());
        }
        order.setStatus(status);
        return toResponse(order);
    }
    @Transactional
    public ApiDtos.OrderResponse cancel(int id, int clientId) {
        Orders order = findOrder(id);
        if (!order.getClient().getId().equals(clientId)) throw new ApiException(HttpStatus.FORBIDDEN, "No puedes cancelar un pedido ajeno");
        if (order.getStatus() != OrderStatusEnum.PENDIENTE) throw new ApiException(HttpStatus.CONFLICT, "Solo se cancelan pedidos PENDIENTES");
        order.setStatus(OrderStatusEnum.CANCELADO);
        order.getProduct().addStock(order.getQuantity());
        return toResponse(order);
    }
    private Orders findOrder(int id) {
        return orders.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }
    private static ApiDtos.OrderResponse toResponse(Orders order) {
        return new ApiDtos.OrderResponse(order.getId(), order.getClient().getId(), order.getClient().getUsername(),
                order.getProduct().getId(), order.getProduct().getName(), order.getQuantity(),
                order.getTotal(), order.getStatus(), order.getCreatedAt());
    }
}
