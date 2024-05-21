package com.ricette.demo.service;

import com.ricette.demo.model.Category;
import com.ricette.demo.model.Ingredient;
import com.ricette.demo.model.Recipe;
import com.ricette.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    public boolean alreadyExists(Category category) {
        return categoryRepository.existsByTitle(category.getTitle());
    }

    public Category findById(Long id) {
        return this.categoryRepository.findById(id).get();
    }

    public List<Recipe> getRecipesOfCategory(Long id) {
        Category c = this.categoryRepository.findById(id).get();
        return c.getRecipes();
    }

    // Metodo per recuperare tutte le categoriea
    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    @Transactional
    public void addCategory(Category category) {
        categoryRepository.save(category);
        System.out.println("category");
    }

    public void update(Category category, Long id) {
        Category c = categoryRepository.findById(id).get();
        c.setTitle(category.getTitle());
        categoryRepository.save(c);
    }

    public void save(Category c) { this.categoryRepository.save(c);
    }

    public void delete(Category category) { this.categoryRepository.delete(category);
    }
}
