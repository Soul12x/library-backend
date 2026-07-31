package com.library.backend.service;

import com.library.backend.dto.EjemplarCreateRequest;
import com.library.backend.dto.EjemplarDetalleResponse;
import com.library.backend.dto.EjemplarResponse;
import com.library.backend.entity.Ejemplar;
import com.library.backend.entity.Libro;
import com.library.backend.exception.BusinessRuleException;
import com.library.backend.exception.ResourceNotFoundException;
import com.library.backend.mapper.EjemplarMapper;
import com.library.backend.repository.EjemplarRepository;
import com.library.backend.repository.LibroRepository;
import com.library.backend.repository.PrestamoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
public class EjemplarService {

    private final EjemplarRepository ejemplarRepository;
    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    @Transactional
    public EjemplarResponse crear(Long libroId, @Valid EjemplarCreateRequest request) {
        Libro libro = obtenerLibroPorId(libroId);
        Ejemplar ejemplar = EjemplarMapper.toEntity(request);
        ejemplar.setLibro(libro);

        Ejemplar ejemplarGuardado = ejemplarRepository.save(ejemplar);
        return EjemplarMapper.toResponse(ejemplarGuardado);
    }

    @Transactional(readOnly = true)
    public EjemplarResponse buscarPorId(Long id) {
        return EjemplarMapper.toResponse(obtenerEjemplarPorId(id));
    }

    @Transactional(readOnly = true)
    public List<EjemplarResponse> listarDisponiblesPorLibro(Long libroId) {
        obtenerLibroPorId(libroId);

        return ejemplarRepository.findDisponiblesByLibroId(libroId).stream()
                .map(EjemplarMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EjemplarDetalleResponse> listarPorLibro(Long libroId) {
        obtenerLibroPorId(libroId);

        Set<Long> idsEjemplaresConPrestamoPendiente = Set.copyOf(
                prestamoRepository.findIdsEjemplaresConPrestamoPendienteByLibroId(libroId)
        );

        return ejemplarRepository.findByLibroId(libroId).stream()
                .map(ejemplar -> EjemplarMapper.toDetalleResponse(
                        ejemplar,
                        libroId,
                        !idsEjemplaresConPrestamoPendiente.contains(ejemplar.getId())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EjemplarResponse> listarDisponiblesPorIsbn(String isbn) {
        Libro libro = libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado."));

        return ejemplarRepository.findDisponiblesByLibroId(libro.getId()).stream()
                .map(EjemplarMapper::toResponse)
                .toList();
    }

    @Transactional
    public void eliminar(Long ejemplarId) {
        Ejemplar ejemplar = ejemplarRepository.findByIdForUpdate(ejemplarId)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado."));

        boolean tienePrestamoPendiente = prestamoRepository.existsByEjemplarIdAndFechaEntregaIsNull(ejemplarId);

        if (tienePrestamoPendiente) {
            throw new BusinessRuleException(
                    "No se puede eliminar el ejemplar porque tiene un préstamo pendiente de devolución."
            );
        }

        if (prestamoRepository.existsByEjemplarId(ejemplarId)) {
            throw new BusinessRuleException("No se puede eliminar el ejemplar porque tiene préstamos históricos asociados.");
        }

        ejemplarRepository.delete(ejemplar);
    }

    private Ejemplar obtenerEjemplarPorId(Long id) {
        return ejemplarRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado."));
    }

    private Libro obtenerLibroPorId(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado."));
    }
}
