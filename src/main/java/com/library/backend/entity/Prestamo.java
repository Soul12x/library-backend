package com.library.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Table(name = "prestamos")
@Getter
@Setter
@NoArgsConstructor
public class Prestamo {

    private static final ZoneId BOGOTA_ZONE_ID = ZoneId.of("America/Bogota");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @NotNull
    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDate fechaDevolucion;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_prestamo_usuario")
    )
    private Usuario usuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ejemplar_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_prestamo_ejemplar")
    )
    private Ejemplar ejemplar;

    @Transient
    public EstadoPrestamo getEstadoPrestamo() {
        if (fechaEntrega != null) {
            return EstadoPrestamo.DEVUELTO;
        }

        LocalDate fechaActual = LocalDate.now(BOGOTA_ZONE_ID);
        return fechaActual.isAfter(fechaDevolucion)
                ? EstadoPrestamo.VENCIDO
                : EstadoPrestamo.ACTIVO;
    }
}
