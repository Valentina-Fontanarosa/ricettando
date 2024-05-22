package com.ricette.demo.controller;

import com.ricette.demo.model.Credentials;
import com.ricette.demo.model.Ingredient;
import com.ricette.demo.model.Recipe;
import com.ricette.demo.model.User;
import com.ricette.demo.service.CategoryService;
import com.ricette.demo.service.CredentialsService;
import com.ricette.demo.service.IngredientService;
import com.ricette.demo.service.RecipeService;
import com.ricette.demo.utility.FileStore;
import com.ricette.demo.validation.IngredientValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

import static com.ricette.demo.model.Ingredient.DIR_ADMIN_PAGES_INGREDIENT;
import static com.ricette.demo.model.Ingredient.DIR_GENERIC_USER_PAGES_INGREDIENT;
import static com.ricette.demo.model.Recipe.*;
import static com.ricette.demo.model.Recipe.DIR_GENERIC_USER_PAGES_RECIPE;

@Controller
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private IngredientValidator ingredientValidator;

    /* ADMIN */

    // -- ELENCO Ingredienti -- //
    @GetMapping("/admin/ingredient")
    public String adminIngredients(Model model) {
        List<Ingredient> ingredients = ingredientService.findAll();
        model.addAttribute("ingredients", ingredients);
        return DIR_ADMIN_PAGES_INGREDIENT + "adminIngredient";
    }

    // -- INSERIMENTO -- //
    @GetMapping("/admin/ingredient/aggiungiIngredient/{idRecipe}")
    public String addIngredientPage(@PathVariable("idRecipe") Long idRecipe, Model model) {
        model.addAttribute("ingredient", new Ingredient());
        model.addAttribute("recipes", this.recipeService.findAll());
        return DIR_ADMIN_PAGES_INGREDIENT + "formIngredient";
    }

    @PostMapping("/admin/ingredient/aggiungiIngredient/{idRecipe}")
    public String addRecipeToIngredient(@Valid @ModelAttribute("ingredient") Ingredient ingredient,
                                  BindingResult bindingResult,
                                  @PathVariable("idRecipe") Long idRecipe,
                                  Model model) {

        //this.ingredientValidator.validate(ingredient, bindingResult);
        if(!bindingResult.hasErrors()) {
            this.recipeService.addIngredient(idRecipe, ingredient);
            return this.adminIngredients(model);
        }
        model.addAttribute("idRecipe", idRecipe);

        return DIR_ADMIN_PAGES_INGREDIENT + "formIngredient";
    }

    // -- MODIFICA -- //
    @GetMapping("/admin/ingredient/modificaIngredient/{idIngredient}")
    public String selezionaIngrediente(@PathVariable("idIngredient") Long idIngredient,
                                       Model model) {
        model.addAttribute("ingredient", this.ingredientService.findById(idIngredient));
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_ADMIN_PAGES_INGREDIENT + "modificaIngredient";
    }

    @PostMapping("/admin/ingredient/modificaIngredient/{idIngredient}")
    public String modificaIngrediente(@Valid @ModelAttribute("ingredient") Ingredient ingredient,
                                      BindingResult bindingResult,
                                      @PathVariable("idIngredient") Long idIngredient,
                                      Model model) {

        ingredient.setId(idIngredient);
        this.ingredientValidator.validate(ingredient, bindingResult);

        if (bindingResult.hasErrors()) {
            this.ingredientService.update(ingredient, idIngredient);
            Ingredient i = this.ingredientService.findById(idIngredient);
            return "redirect:/" + DIR_ADMIN_PAGES_RECIPE + "modificaRecipe/" + i.getRecipe().getId();
        }
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_ADMIN_PAGES_INGREDIENT + "modificaIngredient";
    }

    // -- CANCELLAZIONE -- //
    @GetMapping("/admin/ingredient/eliminaIngredient/{idIngredient}")
    public String deleteIngrediente(@PathVariable("idIngredient") Long idIngredient,
                                    Model model) {
        Ingredient ingredient = this.ingredientService.findById(idIngredient);
        Recipe recipe = this.recipeService.findById(ingredient.getRecipe().getId());
        recipe.getIngredients().remove(ingredient);
        this.ingredientService.delete(ingredient);
        this.recipeService.save(recipe);
        return "redirect:/" + DIR_ADMIN_PAGES_RECIPE + "modificaRecipe/" + ingredient.getRecipe().getId();
    }

    /* GENERIC USER */

    // -- ELENCO Ingredienti -- //
    @GetMapping("/genericUser/ingredient/{idRecipe}")
    public String genericUserIngredients(@PathVariable("idRecipe") Long idRecipe,Model model) {
        // Recupera la ricetta tramite l'ID
        Recipe recipe = recipeService.findById(idRecipe);

        // Verifica che la ricetta esista
        if (recipe != null) {
            // Recupera gli ingredienti associati alla ricetta
            List<Ingredient> ingredients = recipeService.getIngredientsOfRecipe(idRecipe);
            model.addAttribute("ingredients", ingredients);
            model.addAttribute("recipe", recipe);

        } else {
            // Gestisce il caso in cui la ricetta non esista
            model.addAttribute("errorMessage", "Recipe not found");
        }

        return DIR_GENERIC_USER_PAGES_INGREDIENT + "genericUserIngredient";
    }

    // -- INSERIMENTO -- //
    @GetMapping("/genericUser/ingredient/aggiungiIngredientGenericUser/{idRecipe}")
    public String addIngredientPageGenericUser(@PathVariable("idRecipe") Long idRecipe, Model model) {

        //UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials;
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            credentials = credentialsService.getCredentials(userDetails.getUsername());
        } else if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            String email = oauth2User.getAttribute("email");
            credentials = credentialsService.getCredentials(email);
        } else {
            throw new IllegalStateException("Principal type not supported: " + principal.getClass().getName());
        }

        User user = credentials.getUser();

        // Aggiunge una nuova ricetta vuota al modello
        model.addAttribute("ingredient", new Ingredient());

        // Aggiunge le categorie al modello
        model.addAttribute("categories", this.categoryService.findAll());

        // Aggiunge l'utente corrente al modello per assicurarsi che la ricetta sia associata a lui
        model.addAttribute("user", user);

        // Aggiunge l'idRecipe al modello
        model.addAttribute("idRecipe", idRecipe);

        return DIR_GENERIC_USER_PAGES_INGREDIENT + "genericUserformIngredient";
    }

    @PostMapping("/genericUser/ingredient/aggiungiIngredientGenericUser/{idRecipe}")
    public String addIngredientToRecipeGU(@Valid @ModelAttribute("ingredient") Ingredient ingredient,
                                          BindingResult bindingResult,
                                          @PathVariable("idRecipe") Long idRecipe,
                                          Model model) {
        // Ottiene i dettagli dell'utente corrente autenticato
        //UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials;
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            credentials = credentialsService.getCredentials(userDetails.getUsername());
        } else if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            String email = oauth2User.getAttribute("email");
            credentials = credentialsService.getCredentials(email);
        } else {
            throw new IllegalStateException("Principal type not supported: " + principal.getClass().getName());
        }

        User user = credentials.getUser();

        // Recupera la ricetta tramite l'ID
        Recipe recipe = recipeService.findById(idRecipe);

        // Valida l'ingrediente
        //this.ingredientValidator.validate(ingredient, bindingResult);
        if (!bindingResult.hasErrors()) {

            // Associa l'ingrediente alla ricetta
            this.recipeService.addIngredient(idRecipe, ingredient);

            // Ritorna alla lista degli ingredienti associati alla ricetta
            return "redirect:/genericUser/ingredient/" + idRecipe;
        }

        // Se ci sono errori, prepara di nuovo la pagina del form con gli errori e le categorie
        model.addAttribute("user", user);
        model.addAttribute("categories", this.categoryService.findAll());

        return DIR_GENERIC_USER_PAGES_INGREDIENT + "genericUserformIngredient";
    }

    // -- MODIFICA -- //
    @GetMapping("/genericUser/ingredient/modificaIngredient/{idIngredient}")
    public String selezionaIngredienteGenericUser(@PathVariable("idIngredient") Long idIngredient,
                                       Model model) {
        model.addAttribute("ingredient", this.ingredientService.findById(idIngredient));
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_GENERIC_USER_PAGES_INGREDIENT + "genericUserModificaIngredient";
    }

    @PostMapping("/genericUser/ingredient/modificaIngredient/{idIngredient}")
    public String modificaIngredienteGenericUser(@Valid @ModelAttribute("ingredient") Ingredient ingredient,
                                      BindingResult bindingResult,
                                      @PathVariable("idIngredient") Long idIngredient,
                                      Model model) {

        ingredient.setId(idIngredient);

        this.ingredientValidator.validate(ingredient, bindingResult);
        System.out.println("<<<<<<<<<<<< bindingResult.hasErrors()=" + bindingResult.hasErrors());
        if (!bindingResult.hasErrors()) {
            this.ingredientService.update(ingredient, idIngredient);
            Ingredient i = this.ingredientService.findById(idIngredient);
            return genericUserIngredients(i.getRecipe().getId(), model);
        }

        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_GENERIC_USER_PAGES_INGREDIENT + "genericUserModificaIngredient";
    }

    // -- CANCELLAZIONE -- //
    @GetMapping("/genericUser/ingredient/eliminaIngredient/{idIngredient}")
    public String deleteIngredienteGenericUser(@PathVariable("idIngredient") Long idIngredient,
                                               Model model) {
        Ingredient ingredient = this.ingredientService.findById(idIngredient);
        Recipe recipe = this.recipeService.findById(ingredient.getRecipe().getId());
        recipe.getIngredients().remove(ingredient);
        this.ingredientService.delete(ingredient);
        this.recipeService.save(recipe);

        return "redirect:/" + DIR_GENERIC_USER_PAGES_INGREDIENT  + recipe.getId();
    }

}
