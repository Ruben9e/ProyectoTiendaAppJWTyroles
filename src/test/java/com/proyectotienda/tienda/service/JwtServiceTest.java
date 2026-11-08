package com.proyectotienda.tienda.service;

import com.proyectotienda.tienda.entity.Roles;
import com.proyectotienda.tienda.entity.Users;
import com.proyectotienda.tienda.enums.RoleEnum;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.lang.reflect.Field;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Test
    void tokenContainsSubjectAndRequiredClaims() throws Exception {
        Roles role = new Roles(RoleEnum.ADMIN);
        setField(role, "id", 1);
        Users user = new Users("admin", "admin@tienda.local", "not-a-real-password", role);
        setField(user, "id", 17);
        JwtService service = new JwtService(Base64.getEncoder().encodeToString(new byte[32]), 60_000);

        Claims claims = service.validateAndRead(service.generateToken(user));

        assertEquals("admin", claims.getSubject());
        assertEquals(17, claims.get("userId", Integer.class));
        assertEquals(1, claims.get("rolId", Integer.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void initialSqlAdminPasswordMatchesBcryptHash() throws Exception {
        String hash = "$2a$10$r5/QMJP57xBYEIkB0u6jiu5Hbb9YaVHLDpFVJ5pClcYofr0bHyc6i";
        assertTrue(Files.readString(Path.of("tienda.sql")).contains(hash));
        assertTrue(new BCryptPasswordEncoder().matches("password", hash));
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
