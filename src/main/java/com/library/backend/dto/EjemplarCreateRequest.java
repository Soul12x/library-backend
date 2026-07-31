package com.library.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EjemplarCreateRequest(
        @NotBlank @Size(max = 50) String codigoInventario
) {
}
