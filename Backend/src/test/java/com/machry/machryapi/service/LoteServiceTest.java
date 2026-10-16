package com.machry.machryapi.service;

import com.machry.machryapi.dto.item.ItemRequestDTO;
import com.machry.machryapi.dto.lote.LoteRequestDTO;
import com.machry.machryapi.dto.lote.LoteResponseDTO;
import com.machry.machryapi.exception.BusinessRuleException;
import com.machry.machryapi.exception.LoteNotFoundException;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.Lote;
import com.machry.machryapi.model.enums.TipoItem;
import com.machry.machryapi.repository.ItemRepository;
import com.machry.machryapi.repository.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LoteServiceTest {

    private LoteRepository loteRepository;
    private ItemRepository itemRepository;
    private LoteService service;

    @BeforeEach
    void setUp() {
        loteRepository = mock(LoteRepository.class);
        itemRepository = mock(ItemRepository.class);
        service = new LoteService(loteRepository, itemRepository);
    }

    @Test
    void deveCriarLoteValido() {
        Item item = new Item(new ItemRequestDTO("Farinha", TipoItem.INSUMO, "G"));
        LoteRequestDTO request = new LoteRequestDTO(
                1L,
                new BigDecimal("10.5"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 2),
                LocalDate.of(2026, 11, 1)
        );

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(loteRepository.save(any(Lote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoteResponseDTO response = service.create(request);

        assertEquals("Farinha", response.itemNome());
        assertEquals(new BigDecimal("10.5"), response.quantidade());
    }

    @Test
    void naoDeveAceitarValidadeAnteriorAProducao() {
        LoteRequestDTO request = new LoteRequestDTO(
                1L,
                BigDecimal.ONE,
                LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 10, 10),
                LocalDate.of(2026, 10, 9)
        );

        assertThrows(BusinessRuleException.class, () -> service.create(request));
        verifyNoInteractions(itemRepository, loteRepository);
    }

    @Test
    void deveFalharAoBuscarLoteInexistente() {
        when(loteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(LoteNotFoundException.class, () -> service.getById(999L));
    }
}
