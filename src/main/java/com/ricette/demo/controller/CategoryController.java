package com.ricette.demo.controller;

import com.ricette.demo.model.Category;
import com.ricette.demo.model.Recipe;
import com.ricette.demo.service.CategoryService;
import com.ricette.demo.service.RecipeService;
import com.ricette.demo.utility.FileStore;
import com.ricette.demo.validation.CategoryValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.ricette.demo.model.Category.DIR_PAGES_CATEGORY;
import static com.ricette.demo.model.Category.DIR_ADMIN_PAGES_CATEGORY;
import static com.ricette.demo.model.Category.DIR_FOLDER_IMG;
import static com.ricette.demo.model.User.DIR_ADMIN_PAGES_USER;


@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CategoryValidator categoryValidator;

    /* GENERIC USER */

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_PAGES_CATEGORY + "categories";
    }

    @GetMapping("/category/{id}")
    public String getSingleCategory(@PathVariable("id") Long id, Model model) {
        Category category = this.categoryService.findById(id);
        List<Recipe> recipes = this.categoryService.getRecipesOfCategory(id);
        if (recipes.isEmpty()) {
            model.addAttribute("message", "Spiacenti, non ci sono ricette in questa categoria.");
        } else {
            model.addAttribute("recipes", recipes);
        }
        model.addAttribute("category", category);
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_PAGES_CATEGORY + "category";
    }

    // -- ELENCO Categorie -- //
    @GetMapping("/admin/category")
    public String adminCategories(Model model) {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);
        return DIR_ADMIN_PAGES_CATEGORY + "adminCategory";
    }

    // -- INSERIMENTO -- //
    @GetMapping("/admin/category/aggiungiCategory")
    public String addCategoryPage(Model model) {
        model.addAttribute("category", new Category());
        return DIR_ADMIN_PAGES_CATEGORY + "formCategory";
    }

    @PostMapping("/admin/category/aggiungiCategory")
    public String addCategory(@Valid @ModelAttribute("category") Category category,
                                  BindingResult bindingResult,
                                  @RequestParam("file") MultipartFile file,
                                  Model model) {

        this.categoryValidator.validate(category, bindingResult);
        if(!bindingResult.hasErrors()) {
            category.setImg(FileStore.store(file, DIR_FOLDER_IMG));
            this.categoryService.addCategory(category);
            return this.adminCategories(model);
        }
        return DIR_ADMIN_PAGES_CATEGORY + "formCategory";
    }

    // -- MODIFICA -- //
    @GetMapping("/admin/category/modificaCategory/{idCategory}")
    public String selezionaCategoria(@PathVariable("idCategory") Long idCategory,
                                       Model model) {
        model.addAttribute("category", this.categoryService.findById(idCategory));
        return DIR_ADMIN_PAGES_CATEGORY + "modificaCategory";
    }

    @PostMapping("/admin/category/modificaCategory/{idCategory}")
    public String modificaCategoria(@Valid @ModelAttribute("category") Category category,
                                      BindingResult bindingResult,
                                      @PathVariable("idCategory") Long idCategory,
                                      Model model) {
        this.categoryValidator.validate(category, bindingResult);
        category.setId(idCategory);
        if (!bindingResult.hasErrors()) {
            this.categoryService.update(category, category.getId());
            return adminCategories(model);
        }

        Category c = this.categoryService.findById(idCategory);
        category.setImg(c.getImg());
        model.addAttribute("category", this.categoryService.findById(idCategory));
        return DIR_ADMIN_PAGES_CATEGORY + "modificaCategory";
    }

    @PostMapping("/admin/category/changeImg/{idCategory}")
    public String changeImgCategory(@PathVariable("idCategory") Long idCategory,
                                  @RequestParam("file") MultipartFile file, Model model) {

        Category c = this.categoryService.findById(idCategory);
        if(!c.getImg().equals("profili")) {
            FileStore.removeImg(DIR_FOLDER_IMG, c.getImg());
        }

        c.setImg(FileStore.store(file, DIR_FOLDER_IMG));
        this.categoryService.save(c);
        return this.selezionaCategoria(idCategory, model);
    }

    // -- CANCELLAZIONE -- //
    @GetMapping("/admin/category/eliminaCategory/{idCategory}")
    public String deleteCategory(@PathVariable("idCategory") Long idCategory, Model model) {

        Category category = this.categoryService.findById(idCategory);
        List<Recipe> recipes = this.categoryService.getRecipesOfCategory(idCategory);
        if (!recipes.isEmpty()) {
            model.addAttribute("message", "Spiacenti, non puoi cancellare questa categoria, ci sono ricette associate");
        } else {
            // Rimuove l'immagine associata alla categoria dal file system
            FileStore.removeImg(Category.DIR_FOLDER_IMG, category.getImg());
            // Elimina la categoria dal database
            this.categoryService.delete(category);
            //this.categoryService.save(category);
        }
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_ADMIN_PAGES_CATEGORY + "adminCategory";
    }


}
