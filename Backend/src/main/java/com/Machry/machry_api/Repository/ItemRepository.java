package com.Machry.machry_api.Repository;

import com.Machry.machry_api.Model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
