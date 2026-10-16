package com.machry.machryapi.service;

import com.machry.machryapi.dto.item.ItemRequestDTO;
import com.machry.machryapi.dto.item.ItemResponseDTO;
import com.machry.machryapi.exception.ItemNotFoundException;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ItemResponseDTO create(ItemRequestDTO request) {
        Item item = new Item(request);
        Item savedItem = repository.save(item);

        return new ItemResponseDTO(savedItem);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(ItemResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponseDTO getById(Long id) {
        return new ItemResponseDTO(findById(id));
    }

    @Transactional
    public ItemResponseDTO update(Long id, ItemRequestDTO request) {
        Item item = findById(id);
        item.update(request);

        Item updatedItem = repository.save(item);
        return new ItemResponseDTO(updatedItem);
    }

    @Transactional
    public void delete(Long id) {
        Item item = findById(id);
        repository.delete(item);
    }

    private Item findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado: " + id));
    }
}
