package com.ricette.demo.repository;

import com.ricette.demo.model.Category;
import com.ricette.demo.model.User;
import org.springframework.data.jpa.repository.Query;

import com.ricette.demo.model.Recipe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    boolean existsByTitleAndDescription(String title, String description);

    List<Recipe> findTop3ByOrderByIdDesc();

    @Query("SELECT r FROM Recipe r JOIN FETCH r.user u")
    List<Recipe> getRecipesWithNameAndSurnameOfUser();

    @Query("SELECT r, u.name AS userName FROM Recipe r INNER JOIN r.user u WHERE LOWER(r.title) LIKE %:query%")
    List<Recipe> findByTitleContainingWithUserNameIgnoreCase(@Param("query") String query);


    List<Recipe> findByUser(User user);
}
