package com.ricette.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users") // Cambio il nome della tabella
public class User {

    public static final String DIR_PAGES_USER = "informations/user/";
    public static final String DIR_ADMIN_PAGES_USER = "admin/user/";

    public static final String DIR_FOLDER_IMG = "user/profili";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    private LocalDate dateOfBirth;

    private String img;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Recipe> recipes;

    //@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //private Credentials credentials;

    /* methods */
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
