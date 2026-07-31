package com.library.backend.dto;

import com.library.backend.entity.EstadoPrestamo;

import java.time.LocalDate;

public record PrestamoResponse(
        Long id,
        UsuarioResumenResponse usuario,
        EjemplarResumenResponse ejemplar,
        LibroResumenResponse libro,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        LocalDate fechaEntrega,
        EstadoPrestamo estadoPrestamo
) {
}
