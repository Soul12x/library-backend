package com.library.backend.controller;

import com.library.backend.dto.PrestamoCreateRequest;
import com.library.backend.dto.PrestamoResponse;
import com.library.backend.service.PrestamoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<PrestamoResponse> registrar(@Valid @RequestBody PrestamoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.registrar(request));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PrestamoResponse>> listarPorUsuario(
            @PathVariable @Positive Long usuarioId
    ) {
        return ResponseEntity.ok(prestamoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/libro/{libroId}")
    public ResponseEntity<List<PrestamoResponse>> listarPorLibro(
            @PathVariable @Positive Long libroId
    ) {
        return ResponseEntity.ok(prestamoService.listarPorLibro(libroId));
    }

    @PatchMapping("/{id}/devolver")
    public ResponseEntity<PrestamoResponse> devolver(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(prestamoService.devolver(id));
    }
}
