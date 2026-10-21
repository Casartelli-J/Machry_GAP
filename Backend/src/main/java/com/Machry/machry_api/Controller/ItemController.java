package com.Machry.machry_api.Controller;

import com.Machry.machry_api.Service.ItemService;
import com.Machry.machry_api.Model.Item;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.List;
 
@RestController           
@RequestMapping("/itens")  
public class ItemController {
 
    private final ItemService service;
 
    public ItemController(ItemService service) {
        this.service = service;
    }
 
    @GetMapping
    public List<Item> listar() {
        return service.listarTodos();
    }
 
    @GetMapping("/{id}")
    public Item buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item criar(@RequestBody Item item) {
        return service.salvar(item);
    }
 
    @PutMapping("/{id}")
    public Item atualizar(@PathVariable Long id, @RequestBody Item item) {
        return service.atualizar(id, item);
    }
 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}