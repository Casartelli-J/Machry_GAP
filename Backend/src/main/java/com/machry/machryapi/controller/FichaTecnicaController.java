package com.machry.machryapi.controller;

import com.machry.machryapi.dto.fichatecnica.FichaTecnicaRequestDTO;
import com.machry.machryapi.dto.fichatecnica.FichaTecnicaResponseDTO;
import com.machry.machryapi.service.FichaTecnicaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fichas-tecnicas")
public class FichaTecnicaController {

    private final FichaTecnicaService service;

    public FichaTecnicaController(FichaTecnicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FichaTecnicaResponseDTO> create(
            @Valid @RequestBody FichaTecnicaRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<FichaTecnicaResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaTecnicaResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichaTecnicaResponseDTO> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody FichaTecnicaRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
