package com.library.backend.mapper;

import com.library.backend.dto.EjemplarCreateRequest;
import com.library.backend.dto.EjemplarDetalleResponse;
import com.library.backend.dto.EjemplarResponse;
import com.library.backend.entity.Ejemplar;

public final class EjemplarMapper {

    private EjemplarMapper() {
    }

    public static Ejemplar toEntity(EjemplarCreateRequest request) {
        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setCodigoInventario(request.codigoInventario());

        return ejemplar;
    }

    public static EjemplarResponse toResponse(Ejemplar ejemplar) {
        return new EjemplarResponse(
                ejemplar.getId(),
                ejemplar.getCodigoInventario(),
                ejemplar.getLibro().getId()
        );
    }

    public static EjemplarDetalleResponse toDetalleResponse(
            Ejemplar ejemplar,
            Long libroId,
            boolean disponible
    ) {
        return new EjemplarDetalleResponse(
                ejemplar.getId(),
                ejemplar.getCodigoInventario(),
                libroId,
                disponible
        );
    }
}
