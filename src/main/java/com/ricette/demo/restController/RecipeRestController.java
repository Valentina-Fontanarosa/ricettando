package com.ricette.demo.restController;

import com.ricette.demo.model.Recipe;
import com.ricette.demo.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecipeRestController {

    @Autowired
    private RecipeService recipeService;


    @GetMapping(value = "/rest/recipe/{id}")
    public Recipe getRecipe(@PathVariable("id") Long id) {
        return this.recipeService.findById(id);
    }

    @GetMapping(value = "/rest/recipes")
    public List<Recipe> getRecipes() {
        return this.recipeService.findAll();
    }

    /* tutte le ricette che iniziano con 'pa' */
    @GetMapping(value = "/rest/recipe/pa")
    public List<Recipe> getRecipesStartingWithPa() {
        return this.recipeService.findByTitleStartingWith("Pa");
    }

    /* tutte ricette che hanno come ingrediente olio */
    @GetMapping(value = "/rest/recipe/ingredient/olio")
    public List<Recipe> getRecipesWithOlio() {
        return this.recipeService.findByIngredientName("olio");
    }

}
