package com.library.backend.repository;

import com.library.backend.entity.Prestamo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    boolean existsByEjemplarId(Long ejemplarId);

    boolean existsByUsuarioIdAndFechaEntregaIsNull(Long usuarioId);

    boolean existsByEjemplarIdAndFechaEntregaIsNull(Long ejemplarId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select prestamo from Prestamo prestamo where prestamo.id = :id")
    Optional<Prestamo> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select distinct prestamo.ejemplar.id
            from Prestamo prestamo
            where prestamo.ejemplar.libro.id = :libroId
              and prestamo.fechaEntrega is null
            """)
    List<Long> findIdsEjemplaresConPrestamoPendienteByLibroId(@Param("libroId") Long libroId);

    List<Prestamo> findByUsuarioId(Long usuarioId);

    List<Prestamo> findByEjemplarLibroId(Long libroId);
}
