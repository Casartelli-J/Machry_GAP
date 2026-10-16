package com.machry.machryapi.service;

import com.machry.machryapi.dto.etapa.EtapaFichaTecnicaRequestDTO;
import com.machry.machryapi.dto.etapa.EtapaProcessamentoRequestDTO;
import com.machry.machryapi.dto.item.ItemRequestDTO;
import com.machry.machryapi.dto.processamento.ItemProcessamentoRequestDTO;
import com.machry.machryapi.dto.processamento.ProcessamentoRequestDTO;
import com.machry.machryapi.exception.BusinessRuleException;
import com.machry.machryapi.exception.ProcessamentoNotFoundException;
import com.machry.machryapi.model.EtapaFichaTecnica;
import com.machry.machryapi.model.FichaTecnica;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.enums.TipoItem;
import com.machry.machryapi.model.enums.TipoMetodo;
import com.machry.machryapi.repository.FichaTecnicaRepository;
import com.machry.machryapi.repository.ItemRepository;
import com.machry.machryapi.repository.ProcessamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProcessamentoServiceTest {

    private ProcessamentoRepository processamentoRepository;
    private ItemRepository itemRepository;
    private FichaTecnicaRepository fichaRepository;
    private ProcessamentoService service;

    @BeforeEach
    void setUp() {
        processamentoRepository = mock(ProcessamentoRepository.class);
        itemRepository = mock(ItemRepository.class);
        fichaRepository = mock(FichaTecnicaRepository.class);
        service = new ProcessamentoService(processamentoRepository, itemRepository, fichaRepository);
    }

    @Test
    void fichaDeOutroProdutoDeveFalhar() {
        Item produto = itemComId(1L, "Bolo", TipoItem.FINAL, "UN");
        Item outroProduto = itemComId(2L, "Massa", TipoItem.INTERMEDIARIO, "G");
        FichaTecnica ficha = new FichaTecnica();
        ficha.setProduto(outroProduto);
        ReflectionTestUtils.setField(ficha, "id", 10L);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(fichaRepository.findById(10L)).thenReturn(Optional.of(ficha));

        assertThrows(BusinessRuleException.class, () -> service.create(request(1L, 10L, 100L)));
    }

    @Test
    void etapaDuplicadaDeveFalhar() {
        Item produto = itemComId(1L, "Bolo", TipoItem.FINAL, "UN");
        FichaTecnica ficha = new FichaTecnica();
        ficha.setProduto(produto);
        ReflectionTestUtils.setField(ficha, "id", 10L);

        EtapaFichaTecnica etapa = new EtapaFichaTecnica(
                new EtapaFichaTecnicaRequestDTO(1, "Montar", TipoMetodo.MONTAGEM, null, 10)
        );
        ReflectionTestUtils.setField(etapa, "id", 100L);
        ficha.addEtapa(etapa);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(fichaRepository.findById(10L)).thenReturn(Optional.of(ficha));

        ProcessamentoRequestDTO request = new ProcessamentoRequestDTO(
                1L,
                10L,
                BigDecimal.ONE,
                LocalDate.of(2026, 10, 16),
                List.of(new ItemProcessamentoRequestDTO(2L, BigDecimal.ONE)),
                List.of(
                        new EtapaProcessamentoRequestDTO(100L, 11, null),
                        new EtapaProcessamentoRequestDTO(100L, 12, null)
                )
        );

        assertThrows(BusinessRuleException.class, () -> service.create(request));
    }

    @Test
    void deveFalharAoBuscarProcessamentoInexistente() {
        when(processamentoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProcessamentoNotFoundException.class, () -> service.getById(999L));
    }

    private ProcessamentoRequestDTO request(Long produtoId, Long fichaId, Long etapaId) {
        return new ProcessamentoRequestDTO(
                produtoId,
                fichaId,
                BigDecimal.ONE,
                LocalDate.of(2026, 10, 16),
                List.of(new ItemProcessamentoRequestDTO(2L, BigDecimal.ONE)),
                List.of(new EtapaProcessamentoRequestDTO(etapaId, 10, null))
        );
    }

    private Item itemComId(Long id, String nome, TipoItem tipo, String unidade) {
        Item item = new Item(new ItemRequestDTO(nome, tipo, unidade));
        ReflectionTestUtils.setField(item, "id", id);
        return item;
    }
}
