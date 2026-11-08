package com.proyectotienda.tienda.security;

import com.proyectotienda.tienda.enums.RoleEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) return true;
        RequiresRole requirement = method.getMethodAnnotation(RequiresRole.class);
        if (requirement == null) requirement = method.getBeanType().getAnnotation(RequiresRole.class);
        if (requirement == null) return true;

        Object roleAttribute = request.getAttribute("rolId");
        if (!(roleAttribute instanceof Integer roleId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }
        for (RoleEnum role : requirement.value()) {
            if (role.getId() == roleId) return true;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para esta operación");
    }
}
