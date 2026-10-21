package com.machry.machryapi.service;

import com.machry.machryapi.dto.etapa.EtapaFichaTecnicaRequestDTO;
import com.machry.machryapi.dto.fichatecnica.FichaTecnicaRequestDTO;
import com.machry.machryapi.dto.fichatecnica.FichaTecnicaResponseDTO;
import com.machry.machryapi.dto.fichatecnica.IngredienteFichaRequestDTO;
import com.machry.machryapi.dto.item.ItemRequestDTO;
import com.machry.machryapi.exception.BusinessRuleException;
import com.machry.machryapi.model.FichaTecnica;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.enums.TipoItem;
import com.machry.machryapi.model.enums.TipoMetodo;
import com.machry.machryapi.repository.FichaTecnicaRepository;
import com.machry.machryapi.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FichaTecnicaServiceTest {

    private FichaTecnicaRepository fichaRepository;
    private ItemRepository itemRepository;
    private FichaTecnicaService service;

    @BeforeEach
    void setUp() {
        fichaRepository = mock(FichaTecnicaRepository.class);
        itemRepository = mock(ItemRepository.class);
        service = new FichaTecnicaService(fichaRepository, itemRepository);
    }

    @Test
    void deveCriarFichaTecnicaValida() {
        Item produto = item("Massa de Bolo", TipoItem.INTERMEDIARIO, "G");
        Item farinha = item("Farinha", TipoItem.INSUMO, "G");
        FichaTecnicaRequestDTO request = requestValido(10L, 1L);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(farinha));
        when(fichaRepository.existsByProduto_Id(any())).thenReturn(false);
        when(fichaRepository.save(any(FichaTecnica.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FichaTecnicaResponseDTO response = service.create(request);

        assertEquals("Massa de Bolo", response.produtoNome());
        assertEquals(1, response.ingredientes().size());
        assertEquals(1, response.etapas().size());
    }

    @Test
    void insumoNaoPodePossuirFichaTecnica() {
        Item produto = item("Farinha", TipoItem.INSUMO, "G");
        when(itemRepository.findById(10L)).thenReturn(Optional.of(produto));

        assertThrows(BusinessRuleException.class, () -> service.create(requestValido(10L, 1L)));
    }

    @Test
    void naoDeveAceitarEtapasComMesmaOrdem() {
        Item produto = item("Bolo", TipoItem.FINAL, "UN");
        Item ingrediente = item("Massa", TipoItem.INTERMEDIARIO, "G");

        FichaTecnicaRequestDTO request = new FichaTecnicaRequestDTO(
                10L,
                BigDecimal.ONE,
                "UN",
                List.of(new IngredienteFichaRequestDTO(1L, BigDecimal.ONE)),
                List.of(
                        new EtapaFichaTecnicaRequestDTO(1, "Misturar", TipoMetodo.PREPARO, null, 10),
                        new EtapaFichaTecnicaRequestDTO(1, "Assar", TipoMetodo.COCCAO, null, 30)
                )
        );

        when(itemRepository.findById(10L)).thenReturn(Optional.of(produto));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(fichaRepository.existsByProduto_Id(any())).thenReturn(false);

        assertThrows(BusinessRuleException.class, () -> service.create(request));
    }

    private FichaTecnicaRequestDTO requestValido(Long produtoId, Long ingredienteId) {
        return new FichaTecnicaRequestDTO(
                produtoId,
                new BigDecimal("1000"),
                "G",
                List.of(new IngredienteFichaRequestDTO(ingredienteId, new BigDecimal("500"))),
                List.of(new EtapaFichaTecnicaRequestDTO(
                        1,
                        "Misturar",
                        TipoMetodo.PREPARO,
                        "Misturar os ingredientes",
                        10
                ))
        );
    }

    private Item item(String nome, TipoItem tipo, String unidade) {
        return new Item(new ItemRequestDTO(nome, tipo, unidade));
    }
}
