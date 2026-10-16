package com.machry.machryapi.service;

import com.machry.machryapi.exception.BusinessRuleException;
import com.machry.machryapi.dto.etapa.EtapaFichaTecnicaRequestDTO;
import com.machry.machryapi.dto.fichatecnica.FichaTecnicaRequestDTO;
import com.machry.machryapi.dto.fichatecnica.FichaTecnicaResponseDTO;
import com.machry.machryapi.dto.fichatecnica.IngredienteFichaRequestDTO;
import com.machry.machryapi.exception.FichaTecnicaNotFoundException;
import com.machry.machryapi.exception.ItemNotFoundException;
import com.machry.machryapi.model.EtapaFichaTecnica;
import com.machry.machryapi.model.FichaTecnica;
import com.machry.machryapi.model.IngredienteFicha;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.enums.TipoItem;
import com.machry.machryapi.repository.FichaTecnicaRepository;
import com.machry.machryapi.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        validateEtapas(request.etapas());

        FichaTecnica fichaTecnica = new FichaTecnica(request, produto);
        fichaTecnica.replaceIngredientes(buildIngredientes(request.ingredientes()));
        fichaTecnica.replaceEtapas(buildEtapas(request.etapas()));

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
        validateEtapas(request.etapas());

        fichaTecnica.update(request, produto);
        fichaTecnica.replaceIngredientes(buildIngredientes(request.ingredientes()));
        fichaTecnica.replaceEtapas(buildEtapas(request.etapas()));

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

    private List<EtapaFichaTecnica> buildEtapas(List<EtapaFichaTecnicaRequestDTO> etapas) {
        return etapas.stream()
                .map(EtapaFichaTecnica::new)
                .toList();
    }

    private void validateProduto(Item produto) {
        if (produto.getTipo() == TipoItem.INSUMO) {
            throw new BusinessRuleException(
                    "Um item do tipo INSUMO não pode possuir ficha técnica"
            );
        }
    }

    private void validateProdutoSemOutraFicha(Long produtoId, Long fichaIdAtual) {
        boolean possuiOutraFicha = fichaIdAtual == null
                ? fichaTecnicaRepository.existsByProduto_Id(produtoId)
                : fichaTecnicaRepository.existsByProduto_IdAndIdNot(produtoId, fichaIdAtual);

        if (possuiOutraFicha) {
            throw new BusinessRuleException(
                    "O produto já possui uma ficha técnica cadastrada"
            );
        }
    }

    private void validateIngredientes(FichaTecnicaRequestDTO request, Item produto) {
        boolean produtoUsadoComoIngrediente = request.ingredientes()
                .stream()
                .anyMatch(ingrediente -> ingrediente.itemId().equals(produto.getId()));

        if (produtoUsadoComoIngrediente) {
            throw new BusinessRuleException(
                    "O produto não pode ser ingrediente da própria ficha técnica"
            );
        }
    }

    private void validateEtapas(List<EtapaFichaTecnicaRequestDTO> etapas) {
        Set<Integer> ordens = new HashSet<>();

        for (EtapaFichaTecnicaRequestDTO etapa : etapas) {
            if (!ordens.add(etapa.ordem())) {
                throw new BusinessRuleException(
                        "Não pode haver duas etapas com a mesma ordem na ficha técnica"
                );
            }
        }
    }
}
