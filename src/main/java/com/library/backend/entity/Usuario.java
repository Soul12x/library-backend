package com.library.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, length = 254, unique = true)
    private String email;

    @NotNull
    @PastOrPresent
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    private List<Prestamo> prestamos = new ArrayList<>();
}
