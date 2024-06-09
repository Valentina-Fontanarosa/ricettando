package com.ricette.demo.restController;

import com.ricette.demo.model.Ingredient;
import com.ricette.demo.model.User;
import com.ricette.demo.service.IngredientService;
import com.ricette.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserRestController {

    @Autowired
    private UserService userService;


    @GetMapping(value = "/rest/user/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return this.userService.findById(id);
    }

    @GetMapping(value = "/rest/users")
    public List<User> getUsers() {
        return this.userService.findAll();
    }
}
