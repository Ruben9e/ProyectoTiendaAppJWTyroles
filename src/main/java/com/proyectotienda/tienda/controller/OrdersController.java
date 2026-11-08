package com.proyectotienda.tienda.controller;

import com.proyectotienda.tienda.dto.ApiDtos;
import com.proyectotienda.tienda.dto.HttpGlobalResponseDTO;
import com.proyectotienda.tienda.enums.RoleEnum;
import com.proyectotienda.tienda.security.RequiresRole;
import com.proyectotienda.tienda.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    private final OrdersService orders;
    public OrdersController(OrdersService orders) { this.orders = orders; }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresRole(RoleEnum.CLIENTE)
    public HttpGlobalResponseDTO<ApiDtos.OrderResponse> create(@Valid @RequestBody ApiDtos.OrderRequest request,
                                                                HttpServletRequest servletRequest) {
        return new HttpGlobalResponseDTO<>("Pedido creado",
                orders.create(request, (Integer) servletRequest.getAttribute("userId")));
    }
    @GetMapping("/mine")
    @RequiresRole(RoleEnum.CLIENTE)
    public HttpGlobalResponseDTO<List<ApiDtos.OrderResponse>> mine(HttpServletRequest request) {
        return new HttpGlobalResponseDTO<>("Mis pedidos", orders.getMine((Integer) request.getAttribute("userId")));
    }
    @GetMapping("/sales")
    @RequiresRole(RoleEnum.VENDEDOR)
    public HttpGlobalResponseDTO<List<ApiDtos.OrderResponse>> sales(HttpServletRequest request) {
        return new HttpGlobalResponseDTO<>("Ventas de mis productos", orders.getSales((Integer) request.getAttribute("userId")));
    }
    @GetMapping
    @RequiresRole(RoleEnum.ADMIN)
    public HttpGlobalResponseDTO<List<ApiDtos.OrderResponse>> all() {
        return new HttpGlobalResponseDTO<>("Todos los pedidos", orders.getAll());
    }
    @PatchMapping("/{id}/status")
    @RequiresRole({RoleEnum.VENDEDOR, RoleEnum.ADMIN})
    public HttpGlobalResponseDTO<ApiDtos.OrderResponse> status(@PathVariable int id,
            @Valid @RequestBody ApiDtos.OrderStatusRequest request, HttpServletRequest servletRequest) {
        return new HttpGlobalResponseDTO<>("Estado del pedido actualizado", orders.updateStatus(id, request.status(),
                (Integer) servletRequest.getAttribute("userId"), (Integer) servletRequest.getAttribute("rolId")));
    }
    @PatchMapping("/{id}/cancel")
    @RequiresRole(RoleEnum.CLIENTE)
    public HttpGlobalResponseDTO<ApiDtos.OrderResponse> cancel(@PathVariable int id, HttpServletRequest request) {
        return new HttpGlobalResponseDTO<>("Pedido cancelado", orders.cancel(id, (Integer) request.getAttribute("userId")));
    }
}
