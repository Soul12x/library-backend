package com.library.backend.dto;

import com.library.backend.validation.PrestamoFechasValidas;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@PrestamoFechasValidas
public record PrestamoCreateRequest(
        @NotNull @Positive Long usuarioId,
        @NotNull @Positive Long ejemplarId,
        @NotNull LocalDate fechaPrestamo,
        @NotNull LocalDate fechaDevolucion
) {
}
