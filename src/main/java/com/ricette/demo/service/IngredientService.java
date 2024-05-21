package com.ricette.demo.service;

import com.ricette.demo.model.Ingredient;
import com.ricette.demo.model.Recipe;
import com.ricette.demo.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;
    public boolean alreadyExists(Ingredient ingredient) {

        return ingredientRepository.existsByName(ingredient.getName());
    }

    public List<Ingredient> findAll() {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        for(Ingredient c : this.ingredientRepository.findAll()) {
            ingredients.add(c);
        }
        return ingredients;
    }

    public Ingredient findById(Long id) {
        return ingredientRepository.findById(id).get();
    }

    @Transactional
    public void update(Ingredient ingrediente, Long id) {
        Ingredient i = ingredientRepository.findById(id).get();
        i.setName(ingrediente.getName());
        i.setQuantity(ingrediente.getQuantity());
        ingredientRepository.save(i);
    }

    public void delete(Ingredient ingredient) {
        this.ingredientRepository.delete(ingredient);
    }
}
