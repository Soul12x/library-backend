package com.library.backend.validation;

import com.library.backend.dto.PrestamoCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.ZoneId;

public class PrestamoFechasValidasValidator
        implements ConstraintValidator<PrestamoFechasValidas, PrestamoCreateRequest> {

    private static final ZoneId BOGOTA_ZONE_ID = ZoneId.of("America/Bogota");

    @Override
    public boolean isValid(PrestamoCreateRequest request, ConstraintValidatorContext context) {
        if (request == null || request.fechaPrestamo() == null || request.fechaDevolucion() == null) {
            return true;
        }

        boolean esValido = true;
        context.disableDefaultConstraintViolation();

        if (request.fechaPrestamo().isAfter(LocalDate.now(BOGOTA_ZONE_ID))) {
            context.buildConstraintViolationWithTemplate("La fecha de préstamo no puede ser futura.")
                    .addPropertyNode("fechaPrestamo")
                    .addConstraintViolation();
            esValido = false;
        }

        if (request.fechaDevolucion().isBefore(request.fechaPrestamo())) {
            context.buildConstraintViolationWithTemplate(
                            "La fecha de devolución no puede ser anterior a la fecha de préstamo.")
                    .addPropertyNode("fechaDevolucion")
                    .addConstraintViolation();
            esValido = false;
        }

        return esValido;
    }
}
