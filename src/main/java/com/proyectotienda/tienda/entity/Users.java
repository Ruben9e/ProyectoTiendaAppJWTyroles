package com.proyectotienda.tienda.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean active = true;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Roles role;

    protected Users() {}
    public Users(String username, String email, String password, Roles role) {
        this.username = username; this.email = email; this.password = password; this.role = role;
    }
    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public boolean isActive() { return active; }
    public Roles getRole() { return role; }
    public void setRole(Roles role) { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
}
