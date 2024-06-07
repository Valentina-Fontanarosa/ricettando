package com.ricette.demo.service;


import com.ricette.demo.model.Credentials;
import com.ricette.demo.model.Recipe;
import com.ricette.demo.model.User;
import com.ricette.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void save(User user) {
        this.userRepository.save(user);
    }

    public List<User> getUsersWithRecipeCount() {
        return userRepository.getUsersWithRecipeCount();
    }

    public boolean alreadyExists(User c) {
        return this.userRepository.existsByNameAndSurnameAndDateOfBirth(c.getName(),
                c.getSurname(),
                c.getDateOfBirth());
    }


    public List<Recipe> getRecipesOfChef(Long id){
        User c = this.userRepository.findById(id).get();
        return c.getRecipes();
    }

    public User getUser(Long id) {
        Optional<User> result = this.userRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    public List<User> findAll(){
        List<User> users = new ArrayList<User>();
        for(User c : this.userRepository.findAll()) {
            users.add(c);
        }
        return users;
    }

    @Transactional
    public void addRecipe(Long idUser, Recipe recipe) {
        User user = userRepository.findById(idUser).orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        user.getRecipes().add(recipe);
        recipe.setUser(user);
        userRepository.save(user);
    }

    @Transactional
    public void update(User user, Long id) {
        User c = this.userRepository.findById(id).get();
        c.setName(user.getName());
        c.setSurname(user.getSurname());
        c.setDateOfBirth(user.getDateOfBirth());
        c.setRecipes(user.getRecipes());
        this.userRepository.save(c);
    }

    public void delete(User user) {
        this.userRepository.delete(user);
    }

    public User findById(Long id) {
        return this.userRepository.findById(id).get();
    }

}
