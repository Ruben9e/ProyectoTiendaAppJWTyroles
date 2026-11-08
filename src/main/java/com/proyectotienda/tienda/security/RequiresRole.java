package com.proyectotienda.tienda.security;

import com.proyectotienda.tienda.enums.RoleEnum;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRole {
    RoleEnum[] value();
}
