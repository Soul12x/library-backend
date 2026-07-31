package com.library.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = PrestamoFechasValidasValidator.class)
public @interface PrestamoFechasValidas {

    String message() default "Las fechas del préstamo no son válidas.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
