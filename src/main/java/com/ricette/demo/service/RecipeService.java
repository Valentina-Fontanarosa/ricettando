package com.ricette.demo.service;

import com.ricette.demo.model.Category;
import com.ricette.demo.model.Ingredient;
import com.ricette.demo.model.Recipe;
import com.ricette.demo.model.User;
import com.ricette.demo.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {


    @Autowired
    private RecipeRepository recipeRepository;


    public boolean alreadyExists(Recipe p) {
        return recipeRepository.existsByTitleAndDescription(p.getTitle(),
                p.getDescription());
    }

    public List<Recipe> getLastRecipe(){
        return this.recipeRepository.findTop3ByOrderByIdDesc();

    }

    public List<Recipe> getRecipesWithNameAndSurnameOfUser() {
        return recipeRepository.getRecipesWithNameAndSurnameOfUser();
    }

    public Recipe findById(Long id) {
        return this.recipeRepository.findById(id).get();
    }


    public List<Ingredient> getIngredientsOfRecipe(Long id) {
        Recipe c = this.recipeRepository.findById(id).get();
        return c.getIngredients();
    }

    public List<Recipe> findByTitleContainingWithUserNameIgnoreCase(String query) {
        return recipeRepository.findByTitleContainingWithUserNameIgnoreCase(query);
    }

    public List<Recipe> findAll(){
        List<Recipe> recipes = new ArrayList<Recipe>();
        for(Recipe c : this.recipeRepository.findAll()) {
            recipes.add(c);
        }
        return recipes;
    }

    @Transactional
    public void addIngredient(Long idRecipe, Ingredient ingredient) {
        Recipe recipe = recipeRepository.findById(idRecipe).orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        recipe.getIngredients().add(ingredient);
        ingredient.setRecipe(recipe);
        recipeRepository.save(recipe);
    }

    public void update(Recipe recipe, Long id) {
        Recipe r = recipeRepository.findById(id).get();
        r.setTitle(recipe.getTitle());
        r.setDescription(recipe.getDescription());
        r.setProcedure(recipe.getProcedure());
        r.setTime(recipe.getTime());
        r.setLevel(recipe.getLevel());
        r.setCategory(recipe.getCategory());
        r.setIngredients(recipe.getIngredients());
        recipeRepository.save(r);
    }

    @Transactional
    public void save(Recipe r) {
        this.recipeRepository.save(r);
    }

    public void delete(Recipe recipe) {
        this.recipeRepository.delete(recipe);
    }

    public List<Recipe> findByUser(User user) { return this.recipeRepository.findByUser(user);
    }
}
