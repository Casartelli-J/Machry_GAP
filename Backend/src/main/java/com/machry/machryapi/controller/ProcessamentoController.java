package com.machry.machryapi.controller;

import com.machry.machryapi.dto.processamento.ProcessamentoRequestDTO;
import com.machry.machryapi.dto.processamento.ProcessamentoResponseDTO;
import com.machry.machryapi.service.ProcessamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/processamentos")
public class ProcessamentoController {

    private final ProcessamentoService service;

    public ProcessamentoController(ProcessamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProcessamentoResponseDTO> create(
            @Valid @RequestBody ProcessamentoRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProcessamentoResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessamentoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcessamentoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProcessamentoRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
