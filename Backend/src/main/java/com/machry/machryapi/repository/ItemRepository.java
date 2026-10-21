package com.machry.machryapi.repository;

import com.machry.machryapi.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
