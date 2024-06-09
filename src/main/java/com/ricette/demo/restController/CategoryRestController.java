package com.ricette.demo.restController;

import com.ricette.demo.model.Category;
import com.ricette.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryRestController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping(value = "/rest/category/{id}")
    public Category getCategory(@PathVariable("id") Long id) {
        return this.categoryService.findById(id);
    }

    @GetMapping(value = "/rest/categories")
    public List<Category> getCategories() {
        return this.categoryService.findAll();
    }
}
