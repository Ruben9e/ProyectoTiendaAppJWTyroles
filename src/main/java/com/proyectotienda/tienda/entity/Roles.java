package com.proyectotienda.tienda.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Roles {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING) @Column(nullable = false, unique = true, length = 50)
    private com.proyectotienda.tienda.enums.RoleEnum name;

    protected Roles() {}
    public Roles(com.proyectotienda.tienda.enums.RoleEnum name) { this.name = name; }
    public Integer getId() { return id; }
    public com.proyectotienda.tienda.enums.RoleEnum getName() { return name; }
}
