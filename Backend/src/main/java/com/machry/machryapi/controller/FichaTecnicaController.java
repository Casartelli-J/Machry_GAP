package com.machry.machryapi.controller;

import com.machry.machryapi.dto.fichatecnica.FichaTecnicaRequestDTO;
import com.machry.machryapi.dto.fichatecnica.FichaTecnicaResponseDTO;
import com.machry.machryapi.service.FichaTecnicaService;
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
@RequestMapping("/fichas-tecnicas")
@Tag(name = "Fichas Técnicas", description = "Receitas planejadas, ingredientes e etapas")
public class FichaTecnicaController {

    private final FichaTecnicaService service;

    public FichaTecnicaController(FichaTecnicaService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar ficha técnica")
    @ApiResponse(responseCode = "201", description = "Created")
    public ResponseEntity<FichaTecnicaResponseDTO> create(
            @Valid @RequestBody FichaTecnicaRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar fichas técnicas")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<FichaTecnicaResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar ficha técnica por ID")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<FichaTecnicaResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar ficha técnica")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<FichaTecnicaResponseDTO> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody FichaTecnicaRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir ficha técnica")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
