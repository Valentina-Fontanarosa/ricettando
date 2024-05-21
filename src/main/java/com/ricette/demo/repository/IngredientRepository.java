package com.ricette.demo.repository;

import com.ricette.demo.model.Ingredient;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
    boolean existsByName(String name);
}
