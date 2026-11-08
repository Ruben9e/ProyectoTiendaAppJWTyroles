package com.proyectotienda.tienda.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Categories {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    protected Categories() {}
    public Categories(String name) { this.name = name; }
    public Integer getId() { return id; }
    public String getName() { return name; }
}
