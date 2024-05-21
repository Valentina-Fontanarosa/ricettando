package com.ricette.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ingredient {

    public static final String DIR_ADMIN_PAGES_INGREDIENT = "admin/ingredient/";

    public static final String DIR_GENERIC_USER_PAGES_INGREDIENT = "genericUser/ingredient/";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(length = 20)
    private String name;

    @NotNull
    @Column(length = 10)
    private String quantity;

    @ManyToOne
    private Recipe recipe;

    // Costruttori, getter e setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
