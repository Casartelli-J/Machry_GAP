package com.machry.machryapi.service;

import com.machry.machryapi.dto.lote.LoteRequestDTO;
import com.machry.machryapi.dto.lote.LoteResponseDTO;
import com.machry.machryapi.exception.ItemNotFoundException;
import com.machry.machryapi.exception.LoteNotFoundException;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.Lote;
import com.machry.machryapi.repository.ItemRepository;
import com.machry.machryapi.repository.LoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final ItemRepository itemRepository;

    public LoteService(LoteRepository loteRepository, ItemRepository itemRepository) {
        this.loteRepository = loteRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public LoteResponseDTO create(LoteRequestDTO request) {
        validateDates(request);

        Item item = findItemById(request.itemId());
        Lote lote = new Lote(request, item);
        Lote savedLote = loteRepository.save(lote);

        return new LoteResponseDTO(savedLote);
    }

    @Transactional(readOnly = true)
    public List<LoteResponseDTO> getAll() {
        return loteRepository.findAll()
                .stream()
                .map(LoteResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public LoteResponseDTO getById(Long id) {
        return new LoteResponseDTO(findLoteById(id));
    }

    @Transactional
    public LoteResponseDTO update(Long id, LoteRequestDTO request) {
        validateDates(request);

        Lote lote = findLoteById(id);
        Item item = findItemById(request.itemId());

        lote.update(request, item);
        Lote updatedLote = loteRepository.save(lote);

        return new LoteResponseDTO(updatedLote);
    }

    @Transactional
    public void delete(Long id) {
        Lote lote = findLoteById(id);
        loteRepository.delete(lote);
    }

    private Lote findLoteById(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new LoteNotFoundException("Lote não encontrado: " + id));
    }

    private Item findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado: " + id));
    }

    private void validateDates(LoteRequestDTO request) {
        if (request.dataValidade().isBefore(request.dataProducao())) {
            throw new IllegalArgumentException(
                    "A data de validade não pode ser anterior à data de produção"
            );
        }
    }
}
