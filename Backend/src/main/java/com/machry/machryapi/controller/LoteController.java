package com.machry.machryapi.controller;

import com.machry.machryapi.dto.lote.LoteRequestDTO;
import com.machry.machryapi.dto.lote.LoteResponseDTO;
import com.machry.machryapi.service.LoteService;
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
@RequestMapping("/lotes")
@Tag(name = "Lotes", description = "Cadastro e manutenção de lotes")
public class LoteController {

    private final LoteService service;

    public LoteController(LoteService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar lote")
    @ApiResponse(responseCode = "201", description = "Created")
    public ResponseEntity<LoteResponseDTO> create(@Valid @RequestBody LoteRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar lotes")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<LoteResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar lote por ID")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<LoteResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar lote")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<LoteResponseDTO> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody LoteRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir lote")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
