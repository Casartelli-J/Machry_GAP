package com.Machry.machry_api.Service;

import com.Machry.machry_api.Model.Item;
import com.Machry.machry_api.Repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public List<Item> listarTodos() {
        return repository.findAll();
    }

    public Item buscarPorId(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Item não encontrado: " + id));
    }

    public Item salvar(Item item) {
        validar(item);
        item.setId(null); // garante que o banco gere um id novo
        return repository.save(item);
    }

    public Item atualizar(Long id, Item dados) {
        Item existente = buscarPorId(id); // já lança 404 se não existir
        validar(dados);

        existente.setNome(dados.getNome());
        existente.setUnidadeMedida(dados.getUnidadeMedida());
        existente.setTipo(dados.getTipo());

        return repository.save(existente);
    }

    public void deletar(Long id) {
        buscarPorId(id);
        repository.deleteById(id);
    }
    private void validar(Item item) {
        if (item.getNome() == null || item.getNome().isBlank()) {
            throw erro("O nome do item é obrigatório.");
        }
        if (item.getUnidadeMedida() == null || item.getUnidadeMedida().isBlank()) {
            throw erro("A unidade de medida é obrigatória.");
        }
        if (item.getTipo() == null || item.getTipo().isBlank()){
            throw erro("O tipo de produto é obrgatório.");
        }

        }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
