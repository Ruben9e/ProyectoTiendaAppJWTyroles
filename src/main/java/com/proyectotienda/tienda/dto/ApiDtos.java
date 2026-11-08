package com.proyectotienda.tienda.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() {}
    public record RegisterRequest(@NotBlank @Size(max = 50) String username,
                                 @NotBlank @Email @Size(max = 255) String email,
                                 @NotBlank @Size(min = 8, max = 72) String password) {}
    public record LoginRequest(@NotBlank String user, @NotBlank String password) {}
    public record UserCreateRequest(@NotBlank @Size(max = 50) String username,
                                    @NotBlank @Email @Size(max = 255) String email,
                                    @NotBlank @Size(min = 8, max = 72) String password,
                                    @NotNull com.proyectotienda.tienda.enums.RoleEnum role) {}
    public record RoleRequest(@NotNull com.proyectotienda.tienda.enums.RoleEnum role) {}
    public record StatusRequest(@NotNull Boolean active) {}
    public record CategoryRequest(@NotBlank @Size(max = 100) String name) {}
    public record ProductRequest(@NotBlank @Size(max = 150) String name,
                                 @Size(max = 500) String description,
                                 @NotNull @Positive BigDecimal price,
                                 @Min(0) int stock,
                                 @NotNull @Positive Integer categoryId) {}
    public record OrderRequest(@NotNull @Positive Integer productId, @NotNull @Positive Integer quantity) {}
    public record OrderStatusRequest(@NotNull com.proyectotienda.tienda.enums.OrderStatusEnum status) {}
    public record UserResponse(Integer id, String username, String email, boolean active,
                               com.proyectotienda.tienda.enums.RoleEnum role) {}
    public record AuthResponse(String token, UserResponse user) {}
    public record CategoryResponse(Integer id, String name) {}
    public record ProductResponse(Integer id, String name, String description, BigDecimal price, int stock,
                                  Integer categoryId, String categoryName, Integer sellerId, String sellerUsername) {}
    public record ProductPageResponse(List<ProductResponse> content, int page, int size,
                                      long totalElements, int totalPages) {}
    public record OrderResponse(Integer id, Integer clientId, String clientUsername, Integer productId,
                                String productName, int quantity, BigDecimal total,
                                com.proyectotienda.tienda.enums.OrderStatusEnum status,
                                java.time.LocalDateTime createdAt) {}
}
