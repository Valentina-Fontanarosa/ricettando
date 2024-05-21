package com.ricette.demo.validation;

import com.ricette.demo.model.Recipe;
import com.ricette.demo.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class RecipeValidator implements Validator {

    @Autowired
    private RecipeService recipeService;

    @Override
    public void validate(Object o, Errors errors) {
        Recipe recipe = (Recipe)o;

        if (this.recipeService.alreadyExists(recipe)) {
            errors.reject("duplicate.recipe");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Recipe.class.equals(clazz);
    }
}
