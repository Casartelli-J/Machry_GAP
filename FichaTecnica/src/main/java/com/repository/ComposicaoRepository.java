package com.repository;

import com.model.Composicao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComposicaoRepository extends JpaRepository<Composicao, Integer> {
}