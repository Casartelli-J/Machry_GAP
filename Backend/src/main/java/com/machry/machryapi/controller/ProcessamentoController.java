package com.machry.machryapi.controller;

import com.machry.machryapi.dto.processamento.ProcessamentoRequestDTO;
import com.machry.machryapi.dto.processamento.ProcessamentoResponseDTO;
import com.machry.machryapi.service.ProcessamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/processamentos")
@Tag(name = "Processamentos", description = "Registro do que foi efetivamente produzido e utilizado")
public class ProcessamentoController {

    private final ProcessamentoService service;

    public ProcessamentoController(ProcessamentoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar processamento")
    @ApiResponse(responseCode = "201", description = "Created")
    public ResponseEntity<ProcessamentoResponseDTO> create(
            @Valid @RequestBody ProcessamentoRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar processamentos")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<ProcessamentoResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar processamento por ID")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ProcessamentoResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar processamento")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ProcessamentoResponseDTO> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProcessamentoRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir processamento")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
