package com.ricette.demo.validation;

import com.ricette.demo.model.Ingredient;
import com.ricette.demo.service.IngredientService;
import org.springframework.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class IngredientValidator implements Validator {

    @Autowired
    private IngredientService ingredientService;

    @Override
    public void validate(Object o, Errors errors) {
        Ingredient ingredient = (Ingredient)o;

        if (this.ingredientService.alreadyExists(ingredient)) {
            errors.reject("duplicate.ingredient");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Ingredient.class.equals(clazz);
    }
}
