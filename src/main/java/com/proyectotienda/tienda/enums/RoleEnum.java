package com.proyectotienda.tienda.enums;

public enum RoleEnum {
    ADMIN(1), CLIENTE(2), VENDEDOR(3);

    private final int id;

    RoleEnum(int id) { this.id = id; }

    public int getId() { return id; }
}
