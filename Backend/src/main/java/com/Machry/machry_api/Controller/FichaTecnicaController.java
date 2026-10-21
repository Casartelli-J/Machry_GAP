package com.controller;

import com.model.FichaTecnica;
import com.service.FichaTecnicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fichas")
public class FichaTecnicaController {

    private final FichaTecnicaService service;

    public FichaTecnicaController(FichaTecnicaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FichaTecnica> criar(@RequestBody FichaTecnica ficha) {
        return ResponseEntity.ok(service.criar(ficha));
    }

    @GetMapping
    public ResponseEntity<List<FichaTecnica>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaTecnica> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichaTecnica> atualizar(@PathVariable Integer id, @RequestBody FichaTecnica ficha) {
        return ResponseEntity.ok(service.atualizar(id, ficha));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna código 204 (Sucesso, mas sem conteúdo)
    }
}