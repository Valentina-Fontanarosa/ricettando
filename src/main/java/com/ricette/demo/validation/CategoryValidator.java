package com.ricette.demo.validation;

import com.ricette.demo.model.Category;
import com.ricette.demo.service.CategoryService;
import com.ricette.demo.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CategoryValidator implements Validator {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecipeService recipeService;

    @Override
    public void validate(Object o, Errors errors) {
        Category category = (Category)o;

        if (this.categoryService.alreadyExists(category)) {
            errors.reject("duplicate.category");
        }

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Category.class.equals(clazz);
    }
}
