package com.ricette.demo.controller;

import com.ricette.demo.model.Category;
import com.ricette.demo.model.Recipe;
import com.ricette.demo.repository.CategoryRepository;
import com.ricette.demo.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Recupera le ultime 3 ricette dal repository
        Iterable<Recipe> recipes = recipeRepository.findTop3ByOrderByIdDesc();
        Iterable<Recipe> recipesAll = recipeRepository.findAll();

        // Recupera tutte le categorie
        Iterable<Category> categories = categoryRepository.findAll();

        // Aggiungi le ricette e le categorie al modello
        model.addAttribute("recipes", recipes);
        model.addAttribute("recipesAll", recipesAll);
        model.addAttribute("categories", categories);

        return "index";
    }
}
