package com.proyectotienda.tienda.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Products {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(length = 500)
    private String description;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private int stock;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Categories category;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private Users seller;

    protected Products() {}
    public Products(String name, String description, BigDecimal price, int stock, Categories category, Users seller) {
        this.name = name; this.description = description; this.price = price;
        this.stock = stock; this.category = category; this.seller = seller;
    }
    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public Categories getCategory() { return category; }
    public Users getSeller() { return seller; }
    public void update(String name, String description, BigDecimal price, int stock, Categories category) {
        this.name = name; this.description = description; this.price = price;
        this.stock = stock; this.category = category;
    }
    public void reduceStock(int quantity) { this.stock -= quantity; }
    public void addStock(int quantity) { this.stock += quantity; }
}
