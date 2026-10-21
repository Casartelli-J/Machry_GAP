package com.service;

import com.model.FichaTecnica;
import com.repository.FichaTecnicaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FichaTecnicaService {

    private final FichaTecnicaRepository repository;

    public FichaTecnicaService(FichaTecnicaRepository repository) {
        this.repository = repository;
    }

    public FichaTecnica criar(FichaTecnica ficha) {
        // Vincula a ficha técnica a cada ingrediente para o Hibernate salvar corretamente a chave estrangeira
        if (ficha.getIngredientes() != null) {
            ficha.getIngredientes().forEach(ingrediente -> ingrediente.setFichaTecnica(ficha));
        }
        return repository.save(ficha);
    }

    public List<FichaTecnica> listarTodos() {
        return repository.findAll();
    }

    public FichaTecnica buscarPorId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ficha não encontrada"));
    }

    public FichaTecnica atualizar(Integer id, FichaTecnica dadosAtualizados) {
        FichaTecnica ficha = buscarPorId(id);
        
        ficha.setNome(dadosAtualizados.getNome());
        ficha.setRendimento(dadosAtualizados.getRendimento());
        ficha.setUnidadeRendimento(dadosAtualizados.getUnidadeRendimento());
        ficha.setMethod(dadosAtualizados.getMethod());
        
        // Limpa e readiciona para o orphanRemoval do Hibernate gerenciar o banco
        ficha.getIngredientes().clear();
        if (dadosAtualizados.getIngredientes() != null) {
            dadosAtualizados.getIngredientes().forEach(ing -> {
                ing.setFichaTecnica(ficha);
                ficha.getIngredientes().add(ing);
            });
        }
        
        return repository.save(ficha);
    }

    public void deletar(Integer id) {
        repository.deleteById(id);
    }
}