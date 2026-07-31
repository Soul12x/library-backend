package com.library.backend.service;

import com.library.backend.dto.UsuarioRequest;
import com.library.backend.dto.UsuarioResponse;
import com.library.backend.entity.Usuario;
import com.library.backend.exception.BusinessRuleException;
import com.library.backend.exception.ResourceNotFoundException;
import com.library.backend.mapper.UsuarioMapper;
import com.library.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public UsuarioResponse crear(@Valid UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Ya existe un usuario con el email indicado.");
        }

        Usuario usuario = usuarioRepository.save(UsuarioMapper.toEntity(request));
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioMapper.toResponse(obtenerPorId(id));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, @Valid UsuarioRequest request) {
        Usuario usuarioExistente = obtenerPorId(id);

        boolean cambioEmail = !usuarioExistente.getEmail().equals(request.email());
        if (cambioEmail && usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessRuleException("Ya existe un usuario con el email indicado.");
        }

        UsuarioMapper.updateEntity(request, usuarioExistente);
        Usuario usuarioActualizado = usuarioRepository.save(usuarioExistente);

        return UsuarioMapper.toResponse(usuarioActualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.delete(obtenerPorId(id));
    }

    private Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
    }
}
