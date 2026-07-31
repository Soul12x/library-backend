package com.library.backend.dto;

public record LibroResumenResponse(
        Long id,
        String titulo,
        String isbn
) {
}
