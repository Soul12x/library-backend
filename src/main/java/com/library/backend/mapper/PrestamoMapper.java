package com.library.backend.mapper;

import com.library.backend.dto.EjemplarResumenResponse;
import com.library.backend.dto.LibroResumenResponse;
import com.library.backend.dto.PrestamoResponse;
import com.library.backend.dto.UsuarioResumenResponse;
import com.library.backend.entity.Ejemplar;
import com.library.backend.entity.Libro;
import com.library.backend.entity.Prestamo;
import com.library.backend.entity.Usuario;

public final class PrestamoMapper {

    private PrestamoMapper() {
    }

    public static PrestamoResponse toResponse(Prestamo prestamo) {
        Usuario usuario = prestamo.getUsuario();
        Ejemplar ejemplar = prestamo.getEjemplar();
        Libro libro = ejemplar.getLibro();

        UsuarioResumenResponse usuarioResponse = new UsuarioResumenResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido()
        );
        EjemplarResumenResponse ejemplarResponse = new EjemplarResumenResponse(
                ejemplar.getId(),
                ejemplar.getCodigoInventario()
        );
        LibroResumenResponse libroResponse = new LibroResumenResponse(
                libro.getId(),
                libro.getTitulo(),
                libro.getIsbn()
        );

        return new PrestamoResponse(
                prestamo.getId(),
                usuarioResponse,
                ejemplarResponse,
                libroResponse,
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getFechaEntrega(),
                prestamo.getEstadoPrestamo()
        );
    }
}
