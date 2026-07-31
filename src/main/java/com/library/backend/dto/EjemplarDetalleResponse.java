package com.library.backend.dto;

public record EjemplarDetalleResponse(
        Long id,
        String codigoInventario,
        Long libroId,
        boolean disponible
) {
}
