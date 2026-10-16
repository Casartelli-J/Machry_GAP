package com.machry.machryapi.service;

import com.machry.machryapi.dto.processamento.ItemProcessamentoRequestDTO;
import com.machry.machryapi.dto.processamento.ProcessamentoRequestDTO;
import com.machry.machryapi.dto.processamento.ProcessamentoResponseDTO;
import com.machry.machryapi.exception.FichaTecnicaNotFoundException;
import com.machry.machryapi.exception.ItemNotFoundException;
import com.machry.machryapi.exception.ProcessamentoNotFoundException;
import com.machry.machryapi.model.FichaTecnica;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.ItemProcessamento;
import com.machry.machryapi.model.Processamento;
import com.machry.machryapi.model.enums.TipoItem;
import com.machry.machryapi.repository.FichaTecnicaRepository;
import com.machry.machryapi.repository.ItemRepository;
import com.machry.machryapi.repository.ProcessamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProcessamentoService {

    private final ProcessamentoRepository processamentoRepository;
    private final ItemRepository itemRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;

    public ProcessamentoService(
            ProcessamentoRepository processamentoRepository,
            ItemRepository itemRepository,
            FichaTecnicaRepository fichaTecnicaRepository
    ) {
        this.processamentoRepository = processamentoRepository;
        this.itemRepository = itemRepository;
        this.fichaTecnicaRepository = fichaTecnicaRepository;
    }

    @Transactional
    public ProcessamentoResponseDTO create(ProcessamentoRequestDTO request) {
        Item produto = findItemById(request.produtoId());
        FichaTecnica fichaTecnica = findFichaById(request.fichaTecnicaId());

        validateProduto(produto);
        validateFichaPertenceAoProduto(fichaTecnica, produto);

        Processamento processamento = new Processamento(request, produto, fichaTecnica);
        processamento.replaceItensUtilizados(buildItensUtilizados(request.itensUtilizados()));

        Processamento savedProcessamento = processamentoRepository.save(processamento);
        return new ProcessamentoResponseDTO(savedProcessamento);
    }

    @Transactional(readOnly = true)
    public List<ProcessamentoResponseDTO> getAll() {
        return processamentoRepository.findAll()
                .stream()
                .map(ProcessamentoResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProcessamentoResponseDTO getById(Long id) {
        return new ProcessamentoResponseDTO(findProcessamentoById(id));
    }

    @Transactional
    public ProcessamentoResponseDTO update(Long id, ProcessamentoRequestDTO request) {
        Processamento processamento = findProcessamentoById(id);
        Item produto = findItemById(request.produtoId());
        FichaTecnica fichaTecnica = findFichaById(request.fichaTecnicaId());

        validateProduto(produto);
        validateFichaPertenceAoProduto(fichaTecnica, produto);

        processamento.update(request, produto, fichaTecnica);
        processamento.replaceItensUtilizados(buildItensUtilizados(request.itensUtilizados()));

        Processamento updatedProcessamento = processamentoRepository.save(processamento);
        return new ProcessamentoResponseDTO(updatedProcessamento);
    }

    @Transactional
    public void delete(Long id) {
        Processamento processamento = findProcessamentoById(id);
        processamentoRepository.delete(processamento);
    }

    private Processamento findProcessamentoById(Long id) {
        return processamentoRepository.findById(id)
                .orElseThrow(() -> new ProcessamentoNotFoundException(
                        "Processamento não encontrado: " + id
                ));
    }

    private Item findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado: " + id));
    }

    private FichaTecnica findFichaById(Long id) {
        return fichaTecnicaRepository.findById(id)
                .orElseThrow(() -> new FichaTecnicaNotFoundException(
                        "Ficha técnica não encontrada: " + id
                ));
    }

    private List<ItemProcessamento> buildItensUtilizados(
            List<ItemProcessamentoRequestDTO> itensUtilizados
    ) {
        return itensUtilizados.stream()
                .map(itemRequest -> new ItemProcessamento(
                        findItemById(itemRequest.itemId()),
                        itemRequest.quantidadeUtilizada()
                ))
                .toList();
    }

    private void validateProduto(Item produto) {
        if (produto.getTipo() == TipoItem.INSUMO) {
            throw new IllegalArgumentException(
                    "Um item do tipo INSUMO não pode ser produto de um processamento"
            );
        }
    }

    private void validateFichaPertenceAoProduto(FichaTecnica fichaTecnica, Item produto) {
        if (!fichaTecnica.getProduto().getId().equals(produto.getId())) {
            throw new IllegalArgumentException(
                    "A ficha técnica informada não pertence ao produto do processamento"
            );
        }
    }
}
