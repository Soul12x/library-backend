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
        name = "libros",
        uniqueConstraints = @UniqueConstraint(name = "uk_libro_isbn", columnNames = "isbn")
)
@Getter
@Setter
@NoArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String titulo;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false, length = 20, unique = true)
    private String isbn;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String edicion;

    @NotNull
    @PastOrPresent
    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String autor;

    @JsonIgnore
    @OneToMany(mappedBy = "libro", fetch = FetchType.LAZY)
    private List<Ejemplar> ejemplares = new ArrayList<>();
}
