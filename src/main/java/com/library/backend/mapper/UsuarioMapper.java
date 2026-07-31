package com.library.backend.mapper;

import com.library.backend.dto.UsuarioRequest;
import com.library.backend.dto.UsuarioResponse;
import com.library.backend.entity.Usuario;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toEntity(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setFechaNacimiento(request.fechaNacimiento());

        return usuario;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFechaNacimiento()
        );
    }

    public static void updateEntity(UsuarioRequest request, Usuario usuario) {
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setFechaNacimiento(request.fechaNacimiento());
    }
}
