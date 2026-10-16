package com.machry.machryapi.repository;

import com.machry.machryapi.model.FichaTecnica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FichaTecnicaRepository extends JpaRepository<FichaTecnica, Long> {

    boolean existsByProduto_Id(Long produtoId);

    boolean existsByProduto_IdAndIdNot(Long produtoId, Long id);
}
