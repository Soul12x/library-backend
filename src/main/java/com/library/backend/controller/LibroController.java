package com.library.backend.controller;

import com.library.backend.dto.LibroRequest;
import com.library.backend.dto.LibroResponse;
import com.library.backend.service.LibroService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;

    @PostMapping
    public ResponseEntity<LibroResponse> crear(@Valid @RequestBody LibroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<LibroResponse>> listar() {
        return ResponseEntity.ok(libroService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroResponse> buscarPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(libroService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroResponse> actualizar(
            @PathVariable @Positive Long id,
            @Valid @RequestBody LibroRequest request
    ) {
        return ResponseEntity.ok(libroService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        libroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
