package com.ricette.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class Category {

    public static final String DIR_PAGES_CATEGORY = "informations/category/";
    public static final String DIR_ADMIN_PAGES_CATEGORY = "admin/category/";
    public static final String DIR_FOLDER_IMG = "category/profili";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(length = 20)
    private String title;

    private String img;

    @OneToMany (mappedBy = "category")
    private List<Recipe> recipes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

}
