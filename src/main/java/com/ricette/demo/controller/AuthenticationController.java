package com.ricette.demo.controller;

import com.ricette.demo.model.Credentials;
import com.ricette.demo.model.User;
import com.ricette.demo.repository.CategoryRepository;
import com.ricette.demo.service.CategoryService;
import com.ricette.demo.service.CredentialsService;
import com.ricette.demo.service.UserService;
import com.ricette.demo.utility.FileStore;
import com.ricette.demo.validation.CredentialValidator;
import com.ricette.demo.validation.UserValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.ricette.demo.model.User.DIR_FOLDER_IMG;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private CredentialValidator credentialsValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "Authentication/registerForm";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginForm(Model model) {
        return "Authentication/loginForm";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(Model model) {
        return "/";
    }

    @RequestMapping(value="/default", method=RequestMethod.GET)
    public String defaultAfterLogin(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
        model.addAttribute("idUser", credentials.getId());
        model.addAttribute("categories", this.categoryRepository.findAll());
        if(credentials.getRuolo().equals(Credentials.ADMIN_ROLE)) {
            return "admin/dashboardAdmin";
        } else if (credentials.getRuolo().equals(Credentials.GENERIC_USER_ROLE)) {
            return "genericUser/dashboardGenericUser";
        }
        return this.profileUser(model);
    }

    /* PROFILE */
    @GetMapping("/profile")
    public String profileUser(Model model) {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());

        User user = userService.getUser(credentials.getUser().getId());
        model.addAttribute("user", user);
        model.addAttribute("credentials", credentials);
        return "Authentication/profile";
    }

    // AuthenticationController

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult userBindingResult,
                               @Valid @ModelAttribute("credentials") Credentials credentials,
                               BindingResult credentialsBindingResult,
                               Model model) {

        // validazione user e credenziali
        this.userValidator.validate(user, userBindingResult);
        System.out.println("user error: " + userBindingResult.hasErrors());
        this.credentialsValidator.validate(credentials, credentialsBindingResult);

        if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            user.setImg("profile.webp");
            credentials.setUser(user);


            // Salva le credenziali e gestisce eventuali errori
            Credentials savedCredentials = credentialsService.saveCredentials(credentials);
            if (savedCredentials != null) {
                return "Authentication/registrationSuccessful";
            } else {
                // Aggiungi un messaggio di errore nella pagina di registrazione
                model.addAttribute("errorMessage", "Si è verificato un problema durante il salvataggio delle credenziali.");
                return "Authentication/registerForm";
            }

        }
        return "Authentication/registerForm";
    }

    @PostMapping("/changeUserAndPass/{idCred}")
    public String changeUserAndPass(@Valid @ModelAttribute("credentials") Credentials credentials,
                                    BindingResult credentialsBindingResult,
                                    @PathVariable("idCred") Long id,
                                    @RequestParam(name = "confirmPass") String pass,
                                    Model model) {

        credentials.setUsername("defaultUsernameForVa");
        credentialsValidator.validate(credentials, credentialsBindingResult);

        if(!credentials.getPassword().equals(pass)) {
            credentialsBindingResult.addError(new ObjectError("notMatchConfirmPassword", "Le password non coincidono"));
        }

        Credentials c = credentialsService.getCredentials(id);
        User user = userService.getUser(c.getUser().getId());
        credentials.setUsername(c.getUsername());
        credentials.setId(id);

        if(!credentialsBindingResult.hasErrors()) {
            c.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
            credentialsService.save(c);
            model.addAttribute("user", user);
            model.addAttribute("credentials", c);
            model.addAttribute("okChange", true);
            return "Authentication/profile";
        }
        model.addAttribute("user", user);
        model.addAttribute("credentials", credentials);
        model.addAttribute("okChange", false);
        return "Authentication/profile";
    }

    @PostMapping("/changeImgProfile/{idUser}")
    public String changeImgProfile(@PathVariable("idUser") Long id,
                                   @RequestParam("file") MultipartFile file, Model model) {
        User user = userService.getUser(id);
        if(!user.getImg().equals("profile.webp")) {
            FileStore.removeImg(DIR_FOLDER_IMG, user.getImg());
        }
        user.setImg(FileStore.store(file, DIR_FOLDER_IMG));
        userService.saveUser(user);
        return this.profileUser(model);
    }

    // -- MODIFICA PROFILE -- //

    @GetMapping("/modificaProfile/{idUser}")
    public String selezionaProfile(@PathVariable("idUser") Long idUser, Model model) {
        User user = this.userService.findById(idUser);
        model.addAttribute("user", user);
        return "Authentication/profile";
    }

    @PostMapping("/modificaProfile/{idUser}")
    public String modificaProfile(@Valid @ModelAttribute("user") User user,
                                  BindingResult bindingResult,
                                  @PathVariable("idUser") Long idUser,
                                  Model model) {
        this.userValidator.validate(user, bindingResult);
        user.setId(idUser);
        if (!bindingResult.hasErrors()) {
            this.userService.update(user, user.getId());
            return this.profileUser(model);
        }
        User currentUser = this.userService.findById(idUser);
        user.setImg(currentUser.getImg());
        model.addAttribute("user", user);
        return "Authentication/profile";
    }


}

