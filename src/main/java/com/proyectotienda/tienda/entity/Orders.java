package com.proyectotienda.tienda.entity;

import com.proyectotienda.tienda.enums.OrderStatusEnum;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Orders {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Users client;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private OrderStatusEnum status = OrderStatusEnum.PENDIENTE;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Orders() {}
    public Orders(Users client, Products product, int quantity, BigDecimal total) {
        this.client = client; this.product = product; this.quantity = quantity; this.total = total;
    }
    public Integer getId() { return id; }
    public Users getClient() { return client; }
    public Products getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotal() { return total; }
    public OrderStatusEnum getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setStatus(OrderStatusEnum status) { this.status = status; }
}
