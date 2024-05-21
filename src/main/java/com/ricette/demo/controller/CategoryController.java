package com.ricette.demo.controller;

import com.ricette.demo.repository.CategoryRepository;
import com.ricette.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.ricette.demo.model.Category.DIR_PAGES_CATEGORY;


@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    /* GENERIC USER */

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", this.categoryRepository.findAll());
        return "category/list";
    }

    @GetMapping("/category/{id}")
    public String getSingleCategory(@PathVariable("id") Long id, Model model) {
        model.addAttribute("category", this.categoryService.findById(id));
        model.addAttribute("recipes", this.categoryService.getRecipesOfCategory(id));
        return DIR_PAGES_CATEGORY + "category";
    }
}
