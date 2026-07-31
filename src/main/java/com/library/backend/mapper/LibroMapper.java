package com.library.backend.mapper;

import com.library.backend.dto.LibroRequest;
import com.library.backend.dto.LibroResponse;
import com.library.backend.entity.Libro;

public final class LibroMapper {

    private LibroMapper() {
    }

    public static Libro toEntity(LibroRequest request) {
        Libro libro = new Libro();
        libro.setTitulo(request.titulo());
        libro.setIsbn(request.isbn());
        libro.setEdicion(request.edicion());
        libro.setFechaPublicacion(request.fechaPublicacion());
        libro.setAutor(request.autor());

        return libro;
    }

    public static LibroResponse toResponse(Libro libro) {
        return new LibroResponse(
                libro.getId(),
                libro.getTitulo(),
                libro.getIsbn(),
                libro.getEdicion(),
                libro.getFechaPublicacion(),
                libro.getAutor()
        );
    }

    public static void updateEntity(LibroRequest request, Libro libro) {
        libro.setTitulo(request.titulo());
        libro.setIsbn(request.isbn());
        libro.setEdicion(request.edicion());
        libro.setFechaPublicacion(request.fechaPublicacion());
        libro.setAutor(request.autor());
    }
}
