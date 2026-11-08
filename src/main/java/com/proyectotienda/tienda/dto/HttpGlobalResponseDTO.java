package com.proyectotienda.tienda.dto;

public record HttpGlobalResponseDTO<T>(String message, T data) {}
