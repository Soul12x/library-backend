package com.library.backend.dto;

public record EjemplarResponse(
        Long id,
        String codigoInventario,
        Long libroId
) {
}
