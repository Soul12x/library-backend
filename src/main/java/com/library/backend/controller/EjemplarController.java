package com.library.backend.controller;

import com.library.backend.dto.EjemplarCreateRequest;
import com.library.backend.dto.EjemplarDetalleResponse;
import com.library.backend.dto.EjemplarResponse;
import com.library.backend.service.EjemplarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api")
@RequiredArgsConstructor
public class EjemplarController {

    private final EjemplarService ejemplarService;

    @PostMapping("/libros/{libroId}/ejemplares")
    public ResponseEntity<EjemplarResponse> crear(
            @PathVariable @Positive Long libroId,
            @Valid @RequestBody EjemplarCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ejemplarService.crear(libroId, request));
    }

    @GetMapping("/ejemplares/{id}")
    public ResponseEntity<EjemplarResponse> buscarPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(ejemplarService.buscarPorId(id));
    }

    @GetMapping("/libros/{libroId}/ejemplares")
    public ResponseEntity<List<EjemplarDetalleResponse>> listarPorLibro(
            @PathVariable @Positive Long libroId
    ) {
        return ResponseEntity.ok(ejemplarService.listarPorLibro(libroId));
    }

    @GetMapping("/libros/{libroId}/ejemplares/disponibles")
    public ResponseEntity<List<EjemplarResponse>> listarDisponiblesPorLibro(
            @PathVariable @Positive Long libroId
    ) {
        return ResponseEntity.ok(ejemplarService.listarDisponiblesPorLibro(libroId));
    }

    @GetMapping("/libros/isbn/{isbn}/ejemplares/disponibles")
    public ResponseEntity<List<EjemplarResponse>> listarDisponiblesPorIsbn(
            @PathVariable @NotBlank @Size(max = 20) String isbn
    ) {
        return ResponseEntity.ok(ejemplarService.listarDisponiblesPorIsbn(isbn));
    }

    @DeleteMapping("/ejemplares/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        ejemplarService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
