package com.library.backend.service;

import com.library.backend.dto.PrestamoCreateRequest;
import com.library.backend.dto.PrestamoResponse;
import com.library.backend.entity.Ejemplar;
import com.library.backend.entity.Prestamo;
import com.library.backend.entity.Usuario;
import com.library.backend.exception.BusinessRuleException;
import com.library.backend.exception.ResourceNotFoundException;
import com.library.backend.mapper.PrestamoMapper;
import com.library.backend.repository.EjemplarRepository;
import com.library.backend.repository.LibroRepository;
import com.library.backend.repository.PrestamoRepository;
import com.library.backend.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class PrestamoService {

    private static final ZoneId BOGOTA_ZONE_ID = ZoneId.of("America/Bogota");

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EjemplarRepository ejemplarRepository;
    private final LibroRepository libroRepository;

    @Transactional
    public PrestamoResponse registrar(@Valid PrestamoCreateRequest request) {
        Long usuarioId = request.usuarioId();
        Long ejemplarId = request.ejemplarId();
        Usuario usuario = usuarioRepository.findByIdForUpdate(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));
        Ejemplar ejemplar = ejemplarRepository.findByIdForUpdate(ejemplarId)
                .orElseThrow(() -> new ResourceNotFoundException("Ejemplar no encontrado."));

        if (prestamoRepository.existsByUsuarioIdAndFechaEntregaIsNull(usuarioId)) {
            throw new BusinessRuleException("El usuario ya tiene un préstamo pendiente de devolución.");
        }

        if (prestamoRepository.existsByEjemplarIdAndFechaEntregaIsNull(ejemplarId)) {
            throw new BusinessRuleException("El ejemplar ya tiene un préstamo pendiente de devolución.");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(usuario);
        prestamo.setEjemplar(ejemplar);
        prestamo.setFechaPrestamo(request.fechaPrestamo());
        prestamo.setFechaDevolucion(request.fechaDevolucion());
        prestamo.setFechaEntrega(null);

        Prestamo prestamoGuardado = prestamoRepository.save(prestamo);
        return PrestamoMapper.toResponse(prestamoGuardado);
    }

    @Transactional
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = prestamoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Préstamo no encontrado."));

        if (prestamo.getFechaEntrega() != null) {
            throw new BusinessRuleException("El préstamo ya fue devuelto.");
        }

        prestamo.setFechaEntrega(LocalDate.now(BOGOTA_ZONE_ID));
        Prestamo prestamoDevuelto = prestamoRepository.save(prestamo);

        return PrestamoMapper.toResponse(prestamoDevuelto);
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorUsuario(Long usuarioId) {
        verificarUsuarioExiste(usuarioId);
        return prestamoRepository.findByUsuarioId(usuarioId).stream()
                .map(PrestamoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPorLibro(Long libroId) {
        verificarLibroExiste(libroId);
        return prestamoRepository.findByEjemplarLibroId(libroId).stream()
                .map(PrestamoMapper::toResponse)
                .toList();
    }

    private void verificarUsuarioExiste(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado.");
        }
    }

    private void verificarLibroExiste(Long libroId) {
        if (!libroRepository.existsById(libroId)) {
            throw new ResourceNotFoundException("Libro no encontrado.");
        }
    }
}
