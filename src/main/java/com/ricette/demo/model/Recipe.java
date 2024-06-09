package com.ricette.demo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.ricette.demo.utility.FileStore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Entity
public class Recipe {

    public static final String DIR_PAGES_RECIPE = "informations/recipe/";
    public static final String DIR_ADMIN_PAGES_RECIPE = "admin/recipe/";
    public static final String DIR_FOLDER_IMG = "recipe/profili";

    public static final String DIR_GENERIC_USER_PAGES_RECIPE = "genericUser/recipe/";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String title;

    @NotNull
    @Column(length = 2000)
    private String description;

    @NotNull
    @Column(length = 2000)
    private String procedure;

    @NotNull
    private Integer time;

    private Integer level;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    private String img;

    // Constructors, getters, and setters

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    // Include il nome e il cognome dell'utente che ha creato la ricetta
    public String getUserName() {
        if (user != null) {
            return user.getName() + " " + user.getSurname();
        }
        return "";
    }

    // Metodo di supporto per evitare cicli infiniti durante la serializzazione JSON
    @JsonIgnore
    public User getUser() {
        return user;
    }

    // Metodo di supporto per impostare l'utente senza modificare direttamente la classe User
    public void setUserId(Long userId) {
        if (user == null) {
            user = new User();
        }
        user.setId(userId);
    }

    public void eliminaImmagine() {
        FileStore.removeImg(DIR_FOLDER_IMG, this.getImg());
    }
}
