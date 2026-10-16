package com.machry.machryapi.controller;

import com.machry.machryapi.dto.lote.LoteRequestDTO;
import com.machry.machryapi.dto.lote.LoteResponseDTO;
import com.machry.machryapi.service.LoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lotes")
public class LoteController {

    private final LoteService service;

    public LoteController(LoteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LoteResponseDTO> create(@Valid @RequestBody LoteRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<LoteResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoteResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody LoteRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
