package com.ricette.demo.controller;

import com.ricette.demo.model.Credentials;
import com.ricette.demo.model.Recipe;
import com.ricette.demo.model.User;
import com.ricette.demo.service.CategoryService;
import com.ricette.demo.service.CredentialsService;
import com.ricette.demo.service.UserService;
import com.ricette.demo.utility.FileStore;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ricette.demo.validation.UserValidator;

import java.util.List;

import static com.ricette.demo.model.User.*;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private CredentialsService credentialsService;

    /* GENERIC USER */

    @GetMapping("/users")
    public String getUsersWithRecipe(Model model) {
        List<User> users = this.userService.getUsersWithRecipeCount();
        model.addAttribute("users", users);
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_PAGES_USER + "users";
    }

    @GetMapping("/user/{id}")
    public String getSingleUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", this.userService.findById(id));
        model.addAttribute("recipes", this.userService.getRecipesOfChef(id));
        model.addAttribute("categories", this.categoryService.findAll());
        return DIR_PAGES_USER + "user";
    }

    /* ADMIN */

    // -- ELENCO CHEFS -- //
    @GetMapping("/admin/user")
    public String adminUsers(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        List<User> users = this.userService.findAll();
        int i = 0;
        for(User user : users) {
            if (user.getId() == credentials.getId()){
                users.remove(i);
                break;
            }
            i = i+1;
        }
        model.addAttribute("users", users);
        return DIR_ADMIN_PAGES_USER + "adminUser";
    }

    // -- INSERIMENTO -- //
    @GetMapping("/admin/user/aggiungiUser")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return DIR_ADMIN_PAGES_USER + "formUser";
    }

    @PostMapping("/admin/user/aggiungiUser")
    public String aggiungiUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam("file") MultipartFile file,
                               Model model) {
        this.userValidator.validate(user, bindingResult);
        if(!bindingResult.hasErrors()) {
            user.setImg(FileStore.store(file, DIR_FOLDER_IMG));
            this.userService.save(user);
            return this.adminUsers(model);
        }
        return DIR_ADMIN_PAGES_USER + "formUser";
    }

    // -- MODIFICA -- //
    @GetMapping("/admin/user/modificaUser/{id}")
    public String selezionaUser(@PathVariable("id") Long id, Model model) {
        model.addAttribute("user", this.userService.findById(id));
        model.addAttribute("recipes", this.userService.getRecipesOfChef(id));
        return DIR_ADMIN_PAGES_USER + "modificaUser";
    }

    @PostMapping("/admin/user/modificaUser/{idUser}")
    public String modificaUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @PathVariable("idUser") Long idUser,
                               Model model) {
        this.userValidator.validate(user, bindingResult);
        user.setId(idUser);
        if(!bindingResult.hasErrors()) {
            this.userService.update(user, user.getId());
            return this.adminUsers(model);
        }
        User c = this.userService.findById(idUser);
        user.setImg(c.getImg());
        model.addAttribute("recipes", this.userService.getRecipesOfChef(idUser));
        return  DIR_ADMIN_PAGES_USER + "modificaUser";
    }

    @PostMapping("/admin/user/changeImg/{idUser}")
    public String changeImgUser(@PathVariable("idUser") Long idUser,
                                @RequestParam("file") MultipartFile file, Model model) {

        User c = this.userService.findById(idUser);
        if(!c.getImg().equals("profili")) {
            FileStore.removeImg(DIR_FOLDER_IMG, c.getImg());
        }

        c.setImg(FileStore.store(file, DIR_FOLDER_IMG));
        this.userService.save(c);
        return this.selezionaUser(idUser, model);
    }

    // -- CANCELLAZIONE -- //
    @GetMapping("/admin/user/eliminaUser/{id}")
    public String deleteUser(@PathVariable("id") Long id,  Model model) {
        User user = this.userService.findById(id);
        FileStore.removeImg(DIR_FOLDER_IMG, user.getImg());

        //eliminazione immagini a cascata
        user.getRecipes().stream().forEach(recipe -> recipe.eliminaImmagine());

        Credentials c = this.credentialsService.findByUser(user);
        // Verifica se esistono le credenziali associate all'utente
        if (c != null) {
            // Elimina le credenziali
            this.credentialsService.delete(c);
        }
        // Elimina l'utente
        this.userService.delete(user);
        return this.adminUsers(model);
    }

}
