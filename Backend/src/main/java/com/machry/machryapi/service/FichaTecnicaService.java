package com.machry.machryapi.service;

import com.machry.machryapi.dto.fichatecnica.FichaTecnicaRequestDTO;
import com.machry.machryapi.dto.fichatecnica.FichaTecnicaResponseDTO;
import com.machry.machryapi.dto.fichatecnica.IngredienteFichaRequestDTO;
import com.machry.machryapi.exception.FichaTecnicaNotFoundException;
import com.machry.machryapi.exception.ItemNotFoundException;
import com.machry.machryapi.model.FichaTecnica;
import com.machry.machryapi.model.IngredienteFicha;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.enums.TipoItem;
import com.machry.machryapi.repository.FichaTecnicaRepository;
import com.machry.machryapi.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FichaTecnicaService {

    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final ItemRepository itemRepository;

    public FichaTecnicaService(
            FichaTecnicaRepository fichaTecnicaRepository,
            ItemRepository itemRepository
    ) {
        this.fichaTecnicaRepository = fichaTecnicaRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public FichaTecnicaResponseDTO create(FichaTecnicaRequestDTO request) {
        Item produto = findItemById(request.produtoId());

        validateProduto(produto);
        validateProdutoSemOutraFicha(produto.getId(), null);
        validateIngredientes(request, produto);

        FichaTecnica fichaTecnica = new FichaTecnica(request, produto);
        fichaTecnica.replaceIngredientes(buildIngredientes(request.ingredientes()));

        FichaTecnica savedFicha = fichaTecnicaRepository.save(fichaTecnica);
        return new FichaTecnicaResponseDTO(savedFicha);
    }

    @Transactional(readOnly = true)
    public List<FichaTecnicaResponseDTO> getAll() {
        return fichaTecnicaRepository.findAll()
                .stream()
                .map(FichaTecnicaResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public FichaTecnicaResponseDTO getById(Long id) {
        return new FichaTecnicaResponseDTO(findFichaById(id));
    }

    @Transactional
    public FichaTecnicaResponseDTO update(Long id, FichaTecnicaRequestDTO request) {
        FichaTecnica fichaTecnica = findFichaById(id);
        Item produto = findItemById(request.produtoId());

        validateProduto(produto);
        validateProdutoSemOutraFicha(produto.getId(), id);
        validateIngredientes(request, produto);

        fichaTecnica.update(request, produto);
        fichaTecnica.replaceIngredientes(buildIngredientes(request.ingredientes()));

        FichaTecnica updatedFicha = fichaTecnicaRepository.save(fichaTecnica);
        return new FichaTecnicaResponseDTO(updatedFicha);
    }

    @Transactional
    public void delete(Long id) {
        FichaTecnica fichaTecnica = findFichaById(id);
        fichaTecnicaRepository.delete(fichaTecnica);
    }

    private FichaTecnica findFichaById(Long id) {
        return fichaTecnicaRepository.findById(id)
                .orElseThrow(() -> new FichaTecnicaNotFoundException(
                        "Ficha técnica não encontrada: " + id
                ));
    }

    private Item findItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Item não encontrado: " + id));
    }

    private List<IngredienteFicha> buildIngredientes(List<IngredienteFichaRequestDTO> ingredientes) {
        return ingredientes.stream()
                .map(ingredienteRequest -> new IngredienteFicha(
                        findItemById(ingredienteRequest.itemId()),
                        ingredienteRequest.quantidade()
                ))
                .toList();
    }

    private void validateProduto(Item produto) {
        if (produto.getTipo() == TipoItem.INSUMO) {
            throw new IllegalArgumentException(
                    "Um item do tipo INSUMO não pode possuir ficha técnica"
            );
        }
    }

    private void validateProdutoSemOutraFicha(Long produtoId, Long fichaIdAtual) {
        boolean possuiOutraFicha = fichaIdAtual == null
                ? fichaTecnicaRepository.existsByProduto_Id(produtoId)
                : fichaTecnicaRepository.existsByProduto_IdAndIdNot(produtoId, fichaIdAtual);

        if (possuiOutraFicha) {
            throw new IllegalArgumentException(
                    "O produto já possui uma ficha técnica cadastrada"
            );
        }
    }

    private void validateIngredientes(FichaTecnicaRequestDTO request, Item produto) {
        boolean produtoUsadoComoIngrediente = request.ingredientes()
                .stream()
                .anyMatch(ingrediente -> ingrediente.itemId().equals(produto.getId()));

        if (produtoUsadoComoIngrediente) {
            throw new IllegalArgumentException(
                    "O produto não pode ser ingrediente da própria ficha técnica"
            );
        }
    }
}
