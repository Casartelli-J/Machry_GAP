package com.machry.machryapi.controller;

import com.machry.machryapi.dto.item.ItemRequestDTO;
import com.machry.machryapi.dto.item.ItemResponseDTO;
import com.machry.machryapi.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@Tag(name = "Itens", description = "Cadastro e manutenção de itens")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Criar item")
    @ApiResponse(responseCode = "201", description = "Created")
    public ResponseEntity<ItemResponseDTO> create(@Valid @RequestBody ItemRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar itens")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<ItemResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item por ID")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ItemResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<ItemResponseDTO> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody ItemRequestDTO request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir item")
    @ApiResponse(responseCode = "204", description = "No Content")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
