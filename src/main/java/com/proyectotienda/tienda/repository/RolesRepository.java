package com.proyectotienda.tienda.repository;

import com.proyectotienda.tienda.entity.Roles;
import com.proyectotienda.tienda.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolesRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByName(RoleEnum name);
}
