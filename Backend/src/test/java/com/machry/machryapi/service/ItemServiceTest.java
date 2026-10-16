package com.machry.machryapi.service;

import com.machry.machryapi.dto.item.ItemRequestDTO;
import com.machry.machryapi.dto.item.ItemResponseDTO;
import com.machry.machryapi.exception.ItemNotFoundException;
import com.machry.machryapi.model.Item;
import com.machry.machryapi.model.enums.TipoItem;
import com.machry.machryapi.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    private ItemRepository repository;
    private ItemService service;

    @BeforeEach
    void setUp() {
        repository = mock(ItemRepository.class);
        service = new ItemService(repository);
    }

    @Test
    void deveCriarItemValido() {
        ItemRequestDTO request = new ItemRequestDTO("Farinha", TipoItem.INSUMO, "G");
        when(repository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemResponseDTO response = service.create(request);

        assertEquals("Farinha", response.nome());
        assertEquals(TipoItem.INSUMO, response.tipo());
        assertEquals("G", response.unidadeMedida());
        verify(repository).save(any(Item.class));
    }

    @Test
    void deveRetornar404LogicoQuandoItemNaoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.getById(999L));
    }

    @Test
    void deveExcluirItemExistente() {
        Item item = new Item(new ItemRequestDTO("Leite", TipoItem.INSUMO, "ML"));
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        service.delete(1L);

        verify(repository).delete(item);
    }
}
