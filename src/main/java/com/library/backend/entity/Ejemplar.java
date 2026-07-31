package com.library.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "ejemplares",
        uniqueConstraints = @UniqueConstraint(name = "uk_ejemplar_codigo_inventario", columnNames = "codigo_inventario")
)
@Getter
@Setter
@NoArgsConstructor
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "codigo_inventario", nullable = false, length = 50, unique = true)
    private String codigoInventario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false) //LAZY ahorra memoria y consultas innecesarias.
    @JoinColumn(
            name = "libro_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ejemplar_libro")
    )
    private Libro libro;

    @JsonIgnore
    @OneToMany(mappedBy = "ejemplar", fetch = FetchType.LAZY)
    private List<Prestamo> prestamos = new ArrayList<>();
}
