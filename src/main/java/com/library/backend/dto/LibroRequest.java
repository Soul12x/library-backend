package com.library.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record LibroRequest(
        @NotBlank @Size(max = 255) String titulo,
        @NotBlank @Size(max = 20) String isbn,
        @NotBlank @Size(max = 100) String edicion,
        @NotNull @PastOrPresent LocalDate fechaPublicacion,
        @NotBlank @Size(max = 200) String autor
) {
}
