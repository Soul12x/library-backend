package com.library.backend.repository;

import com.library.backend.entity.Ejemplar;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {

    List<Ejemplar> findByLibroId(Long libroId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ejemplar from Ejemplar ejemplar where ejemplar.id = :id")
    Optional<Ejemplar> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select ejemplar
            from Ejemplar ejemplar
            where ejemplar.libro.id = :libroId
              and not exists (
                  select prestamo
                  from Prestamo prestamo
                  where prestamo.ejemplar = ejemplar
                    and prestamo.fechaEntrega is null
              )
            """)
    List<Ejemplar> findDisponiblesByLibroId(@Param("libroId") Long libroId);
}
