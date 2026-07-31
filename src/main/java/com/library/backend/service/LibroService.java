package com.library.backend.service;

import com.library.backend.dto.LibroRequest;
import com.library.backend.dto.LibroResponse;
import com.library.backend.entity.Libro;
import com.library.backend.exception.BusinessRuleException;
import com.library.backend.exception.ResourceNotFoundException;
import com.library.backend.mapper.LibroMapper;
import com.library.backend.repository.LibroRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;

    @Transactional
    public LibroResponse crear(@Valid LibroRequest request) {
        if (libroRepository.existsByIsbn(request.isbn())) {
            throw new BusinessRuleException("Ya existe un libro con el ISBN indicado.");
        }

        Libro libro = libroRepository.save(LibroMapper.toEntity(request));
        return LibroMapper.toResponse(libro);
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> listar() {
        return libroRepository.findAll().stream()
                .map(LibroMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LibroResponse buscarPorId(Long id) {
        return LibroMapper.toResponse(obtenerPorId(id));
    }

    @Transactional
    public LibroResponse actualizar(Long id, @Valid LibroRequest request) {
        Libro libroExistente = obtenerPorId(id);

        boolean cambioIsbn = !libroExistente.getIsbn().equals(request.isbn());
        if (cambioIsbn && libroRepository.existsByIsbn(request.isbn())) {
            throw new BusinessRuleException("Ya existe un libro con el ISBN indicado.");
        }

        LibroMapper.updateEntity(request, libroExistente);
        Libro libroActualizado = libroRepository.save(libroExistente);

        return LibroMapper.toResponse(libroActualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        libroRepository.delete(obtenerPorId(id));
    }

    private Libro obtenerPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado."));
    }
}
