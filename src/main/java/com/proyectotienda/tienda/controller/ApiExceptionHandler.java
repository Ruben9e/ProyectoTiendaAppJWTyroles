package com.proyectotienda.tienda.controller;

import com.proyectotienda.tienda.dto.HttpGlobalResponseDTO;
import com.proyectotienda.tienda.service.ApiException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> handleApi(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new HttpGlobalResponseDTO<>(ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new HttpGlobalResponseDTO<>(message, null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new HttpGlobalResponseDTO<>("El cuerpo de la solicitud no es válido", null));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> handleStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status).body(new HttpGlobalResponseDTO<>(ex.getReason(), null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> handleConstraint(DataIntegrityViolationException ex) {
        log.warn("Conflicto de integridad de datos", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new HttpGlobalResponseDTO<>("El registro está duplicado o relacionado con otros datos", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpGlobalResponseDTO<Void>> handleUnexpected(Exception ex) {
        log.error("Error no controlado al procesar la solicitud", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new HttpGlobalResponseDTO<>("Error interno del servidor", null));
    }
}
