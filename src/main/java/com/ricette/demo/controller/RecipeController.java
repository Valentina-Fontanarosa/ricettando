package com.ricette.demo.controller;

import com.ricette.demo.model.*;
import com.ricette.demo.service.CategoryService;
import com.ricette.demo.service.CredentialsService;
import com.ricette.demo.service.UserService;
import com.ricette.demo.service.RecipeService;
import com.ricette.demo.utility.FileStore;
import com.ricette.demo.validation.RecipeValidator;
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

import static com.ricette.demo.model.Recipe.*;
import static com.ricette.demo.model.User.DIR_ADMIN_PAGES_USER;

@Controller
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecipeValidator recipeValidator;

    /* GENERIC USER */

    @GetMapping("/recipes")
    public String getRecipesWithUser(Model model) {
        // Ricette con nome e cognome dell'utente
        List<Recipe> recipes = this.recipeService.getRecipesWithNameAndSurnameOfUser();
        model.addAttribute("recipes", recipes);
        // Ultime tre ricette
        List<Recipe> lastThreeRecipes = this.recipeService.getLastRecipe();
        model.addAttribute("lastThreeRecipes", lastThreeRecipes);

        model.addAttribute("categories", this.categoryService.findAll());

        return DIR_PAGES_RECIPE + "recipes";
    }

    @GetMapping("/recipe/{id}")
    public String getSingleRecipe(@PathVariable("id") Long id, Model model) {
        model.addAttribute("recipe", this.recipeService.findById(id));
        model.addAttribute("ingredients", this.recipeService.getIngredientsOfRecipe(id));
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_PAGES_RECIPE + "recipe";
    }

    @GetMapping("/search")
    public String searchRecipes(@RequestParam("query") String query, Model model) {
        List<Recipe> recipesWithUserNames = this.recipeService.findByTitleContainingWithUserNameIgnoreCase(query);
        if (recipesWithUserNames.isEmpty()) {
            model.addAttribute("message", "Spiacenti, la ricerca non è andata a buon fine o non ci sono ricette corrispondenti.");
        } else {
            model.addAttribute("recipesWithUserNames", recipesWithUserNames);
            model.addAttribute("categories", this.categoryService.findAll());
        }
        return DIR_PAGES_RECIPE + "searchRecipe";
    }

    /* ADMIN */

    // -- ELENCO Recipe -- //
    @GetMapping("/admin/recipe")
    public String adminRecipes(Model model) {
        List<Recipe> recipes = recipeService.findAll();
        model.addAttribute("recipes", recipes);
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_ADMIN_PAGES_RECIPE + "adminRecipe";
    }
    // -- INSERIMENTO -- //
    @GetMapping("/admin/recipe/aggiungiRecipe/{idUser}")
    public String addRecipePage(@PathVariable("idUser") Long idUser,Model model) {
        model.addAttribute("recipe", new Recipe());
        model.addAttribute("categories", this.categoryService.findAll());
        model.addAttribute("users", this.userService.findAll());
        return DIR_ADMIN_PAGES_RECIPE + "formRecipe";
    }

    @PostMapping("/admin/recipe/aggiungiRecipe/{idUser}")
    public String addRecipeToChef(@Valid @ModelAttribute("recipe") Recipe recipe,
                                  BindingResult bindingResult,
                                  @PathVariable("idUser") Long idUser,
                                  @RequestParam("file") MultipartFile file,
                                  Model model) {

        this.recipeValidator.validate(recipe, bindingResult);
        if(!bindingResult.hasErrors()) {
            recipe.setImg(FileStore.store(file, DIR_FOLDER_IMG));
            this.userService.addRecipe(idUser, recipe);
            return this.adminRecipes(model);
        }
        model.addAttribute("idUser", idUser);
        model.addAttribute("categories", this.categoryService.findAll());

        return DIR_ADMIN_PAGES_RECIPE + "formRecipe";
    }

    // -- MODIFICA -- //
    @GetMapping("/admin/recipe/modificaRecipe/{id}")
    public String selezionaRecipe(@PathVariable("id") Long id, Model model) {
        model.addAttribute("recipe", this.recipeService.findById(id));
        model.addAttribute("ingredients", this.recipeService.getIngredientsOfRecipe(id));
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_ADMIN_PAGES_RECIPE + "modificaRecipe";
    }

    @PostMapping("/admin/recipe/modificaRecipe/{idRecipe}")
    public String modificaRecipe(@Valid @ModelAttribute("recipe") Recipe recipe,
                                 BindingResult bindingResult,
                                 @PathVariable("idRecipe") Long idRecipe,
                                 Model model) {
        this.recipeValidator.validate(recipe, bindingResult);
        recipe.setId(idRecipe);
        if(!bindingResult.hasErrors()) {
            this.recipeService.update(recipe, recipe.getId());
            return this.adminRecipes(model);
        }
        Recipe r = this.recipeService.findById(idRecipe);
        recipe.setImg(r.getImg());
        model.addAttribute("ingredients", this.recipeService.getIngredientsOfRecipe(recipe.getId()));
        return DIR_ADMIN_PAGES_RECIPE + "modificaRecipe";
    }

    @PostMapping("/admin/recipe/changeImg/{idRecipe}")
    public String changeImgRecipe(@PathVariable("idRecipe") Long idRecipe,
                                  @RequestParam("file") MultipartFile file, Model model) {

        Recipe r = this.recipeService.findById(idRecipe);
        if(!r.getImg().equals("profili")) {
            FileStore.removeImg(DIR_FOLDER_IMG, r.getImg());
        }

        r.setImg(FileStore.store(file, DIR_FOLDER_IMG));
        this.recipeService.save(r);
        return this.selezionaRecipe(idRecipe, model);
    }

    // -- CANCELLAZIONE -- //
    @GetMapping("/admin/recipe/eliminaRecipe/{idRecipe}")
    public String deleteRecipe(@PathVariable("idRecipe") Long idRecipe,
                               Model model) {
        Recipe recipe = this.recipeService.findById(idRecipe);
        FileStore.removeImg(DIR_FOLDER_IMG, recipe.getImg());
        recipe.getUser().getRecipes().remove(recipe);
        this.recipeService.delete(recipe);
        this.userService.save(recipe.getUser());
        return "redirect:/" + DIR_ADMIN_PAGES_USER + "modificaUser/" + recipe.getUser().getId();
    }

    /* GENERIC USER */

    /* ELENCO RICETTE dello chef */

    @GetMapping("/genericUser/recipesGenericUser")
    public String getGenericUserRecipes(Model model) {
        // Ottiene i dettagli dell'utente corrente autenticato
        //UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Recupera le credenziali dell'utente basate sul nome utente
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

        // Recupera l'utente dalle credenziali
        User user = credentials.getUser();

        // Trova tutte le ricette associate all'utente corrente
        List<Recipe> recipes = recipeService.findByUser(user);

        // Aggiunge la lista delle ricette al modello per renderle disponibili nel template
        model.addAttribute("recipes", recipes);
        model.addAttribute("categories", this.categoryService.findAll());

        return DIR_GENERIC_USER_PAGES_RECIPE + "genericUserRecipe";
    }

    // -- INSERIMENTO -- //

    @GetMapping("/genericUser/recipe/aggiungiRecipeGenericUser")
    public String addRecipePageGenericUser(Model model) {

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
        model.addAttribute("recipe", new Recipe());

        // Aggiunge le categorie al modello
        model.addAttribute("categories", this.categoryService.findAll());

        // Aggiunge l'utente corrente al modello per assicurarsi che la ricetta sia associata a lui
        model.addAttribute("user", user);

        return DIR_GENERIC_USER_PAGES_RECIPE+ "genericUserformRecipe";
    }

    @PostMapping("/genericUser/recipe/aggiungiRecipeGenericUser")
    public String addRecipeToGenericUser(@Valid @ModelAttribute("recipe") Recipe recipe,
                                  BindingResult bindingResult,
                                  @RequestParam("file") MultipartFile file,
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

        // Valida la ricetta
        this.recipeValidator.validate(recipe, bindingResult);
        if (!bindingResult.hasErrors()) {
            recipe.setImg(FileStore.store(file, DIR_FOLDER_IMG));
            // Associa la ricetta all'utente autenticato
            this.userService.addRecipe(user.getId(), recipe);
            // Ritorna alla lista delle ricette (aggiornando il modello)
            return this.getGenericUserRecipes(model);
        }
        // Se ci sono errori, prepara di nuovo la pagina del form con gli errori e le categorie
        model.addAttribute("user", user);
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_GENERIC_USER_PAGES_RECIPE + "genericUserformRecipe";
    }

    // -- MODIFICA -- //
    @GetMapping("/genericUser/recipe/modificaRecipe/{id}")
    public String selezionaRecipeGenericUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("recipe", this.recipeService.findById(id));
        model.addAttribute("ingredients", this.recipeService.getIngredientsOfRecipe(id));
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_GENERIC_USER_PAGES_RECIPE + "genericUserModificaRecipe";
    }

    @PostMapping("/genericUser/recipe/modificaRecipe/{idRecipe}")
    public String modificaRecipeGenericUser(@Valid @ModelAttribute("recipe") Recipe recipe,
                                 BindingResult bindingResult,
                                 @PathVariable("idRecipe") Long idRecipe,
                                 Model model) {
        this.recipeValidator.validate(recipe, bindingResult);
        recipe.setId(idRecipe);
        if(!bindingResult.hasErrors()) {
            this.recipeService.update(recipe, recipe.getId());
            return this.getGenericUserRecipes(model);
        }
        Recipe r = this.recipeService.findById(idRecipe);
        recipe.setImg(r.getImg());
        model.addAttribute("ingredients", this.recipeService.getIngredientsOfRecipe(recipe.getId()));
        return DIR_GENERIC_USER_PAGES_RECIPE + "genericUserModificaRecipe";
    }

    @PostMapping("/genericUser/recipe/changeImg/{idRecipe}")
    public String changeImgRecipeGenericUser(@PathVariable("idRecipe") Long idRecipe,
                                  @RequestParam("file") MultipartFile file, Model model) {

        Recipe r = this.recipeService.findById(idRecipe);
        if(!r.getImg().equals("profili")) {
            FileStore.removeImg(DIR_FOLDER_IMG, r.getImg());
        }

        r.setImg(FileStore.store(file, DIR_FOLDER_IMG));
        this.recipeService.save(r);
        return this.selezionaRecipe(idRecipe, model);
    }

    // -- CANCELLAZIONE -- //
    @GetMapping("/genericUser/recipe/eliminaRecipe/{idRecipe}")
    public String deleteRecipeGenericUser(@PathVariable("idRecipe") Long idRecipe,
                               Model model) {
        Recipe recipe = this.recipeService.findById(idRecipe);
        FileStore.removeImg(DIR_FOLDER_IMG, recipe.getImg());
        recipe.getUser().getRecipes().remove(recipe);
        this.recipeService.delete(recipe);
        this.userService.save(recipe.getUser());
        return "redirect:/genericUser/recipesGenericUser";
    }

}
