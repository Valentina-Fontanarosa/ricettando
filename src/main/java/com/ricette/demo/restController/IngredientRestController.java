package com.ricette.demo.restController;

import com.ricette.demo.model.Category;
import com.ricette.demo.model.Ingredient;
import com.ricette.demo.service.CategoryService;
import com.ricette.demo.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IngredientRestController {

    @Autowired
    private IngredientService ingredientService;


    @GetMapping(value = "/rest/ingredient/{id}")
    public Ingredient getIngredient(@PathVariable("id") Long id) {
        return this.ingredientService.findById(id);
    }

    @GetMapping(value = "/rest/ingredients")
    public List<Ingredient> getIngredients() {
        return this.ingredientService.findAll();
    }
}
