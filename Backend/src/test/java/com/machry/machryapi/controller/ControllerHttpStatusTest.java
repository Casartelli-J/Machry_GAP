package com.machry.machryapi.controller;

import com.machry.machryapi.exception.GlobalExceptionHandler;
import com.machry.machryapi.service.FichaTecnicaService;
import com.machry.machryapi.service.ItemService;
import com.machry.machryapi.service.LoteService;
import com.machry.machryapi.service.ProcessamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ControllerHttpStatusTest {

    private static final String ITEM_JSON = """
            {
              "nome": "Farinha",
              "tipo": "INSUMO",
              "unidadeMedida": "G"
            }
            """;

    private static final String LOTE_JSON = """
            {
              "itemId": 1,
              "quantidade": 10,
              "dataProducao": "2026-10-01",
              "dataEntrada": "2026-10-02",
              "dataValidade": "2026-11-01"
            }
            """;

    private static final String FICHA_JSON = """
            {
              "produtoId": 1,
              "rendimento": 1,
              "unidadeRendimento": "UN",
              "ingredientes": [
                {"itemId": 2, "quantidade": 100}
              ],
              "etapas": [
                {
                  "ordem": 1,
                  "nome": "Misturar",
                  "tipoMetodo": "PREPARO",
                  "descricao": "Misturar ingredientes",
                  "tempoPrevistoMinutos": 10
                }
              ]
            }
            """;

    private static final String PROCESSAMENTO_JSON = """
            {
              "produtoId": 1,
              "fichaTecnicaId": 1,
              "quantidadeProduzida": 1,
              "dataProcessamento": "2026-10-16",
              "itensUtilizados": [
                {"itemId": 2, "quantidadeUtilizada": 100}
              ],
              "etapasRealizadas": [
                {
                  "etapaFichaTecnicaId": 1,
                  "tempoRealizadoMinutos": 12,
                  "observacao": "Teste"
                }
              ]
            }
            """;

    @Test
    void itemDeveUsarStatusHttpCorretos() throws Exception {
        ItemService service = mock(ItemService.class);
        when(service.getAll()).thenReturn(List.of());
        MockMvc mvc = mvc(new ItemController(service));

        mvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON).content(ITEM_JSON))
                .andExpect(status().isCreated());
        mvc.perform(get("/items")).andExpect(status().isOk());
        mvc.perform(get("/items/1")).andExpect(status().isOk());
        mvc.perform(put("/items/1").contentType(MediaType.APPLICATION_JSON).content(ITEM_JSON))
                .andExpect(status().isOk());
        mvc.perform(delete("/items/1")).andExpect(status().isNoContent());
    }

    @Test
    void loteDeveUsarStatusHttpCorretos() throws Exception {
        LoteService service = mock(LoteService.class);
        when(service.getAll()).thenReturn(List.of());
        MockMvc mvc = mvc(new LoteController(service));

        mvc.perform(post("/lotes").contentType(MediaType.APPLICATION_JSON).content(LOTE_JSON))
                .andExpect(status().isCreated());
        mvc.perform(get("/lotes")).andExpect(status().isOk());
        mvc.perform(get("/lotes/1")).andExpect(status().isOk());
        mvc.perform(put("/lotes/1").contentType(MediaType.APPLICATION_JSON).content(LOTE_JSON))
                .andExpect(status().isOk());
        mvc.perform(delete("/lotes/1")).andExpect(status().isNoContent());
    }

    @Test
    void fichaTecnicaDeveUsarStatusHttpCorretos() throws Exception {
        FichaTecnicaService service = mock(FichaTecnicaService.class);
        when(service.getAll()).thenReturn(List.of());
        MockMvc mvc = mvc(new FichaTecnicaController(service));

        mvc.perform(post("/fichas-tecnicas").contentType(MediaType.APPLICATION_JSON).content(FICHA_JSON))
                .andExpect(status().isCreated());
        mvc.perform(get("/fichas-tecnicas")).andExpect(status().isOk());
        mvc.perform(get("/fichas-tecnicas/1")).andExpect(status().isOk());
        mvc.perform(put("/fichas-tecnicas/1").contentType(MediaType.APPLICATION_JSON).content(FICHA_JSON))
                .andExpect(status().isOk());
        mvc.perform(delete("/fichas-tecnicas/1")).andExpect(status().isNoContent());
    }

    @Test
    void processamentoDeveUsarStatusHttpCorretos() throws Exception {
        ProcessamentoService service = mock(ProcessamentoService.class);
        when(service.getAll()).thenReturn(List.of());
        MockMvc mvc = mvc(new ProcessamentoController(service));

        mvc.perform(post("/processamentos").contentType(MediaType.APPLICATION_JSON).content(PROCESSAMENTO_JSON))
                .andExpect(status().isCreated());
        mvc.perform(get("/processamentos")).andExpect(status().isOk());
        mvc.perform(get("/processamentos/1")).andExpect(status().isOk());
        mvc.perform(put("/processamentos/1").contentType(MediaType.APPLICATION_JSON).content(PROCESSAMENTO_JSON))
                .andExpect(status().isOk());
        mvc.perform(delete("/processamentos/1")).andExpect(status().isNoContent());
    }

    @Test
    void exclusaoComDependenciaDeveRetornar409() throws Exception {
        ItemService service = mock(ItemService.class);
        doThrow(new DataIntegrityViolationException("foreign key"))
                .when(service).delete(1L);

        MockMvc mvc = mvc(new ItemController(service));

        mvc.perform(delete("/items/1"))
                .andExpect(status().isConflict());
    }

    private MockMvc mvc(Object controller) {
        return MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
}
