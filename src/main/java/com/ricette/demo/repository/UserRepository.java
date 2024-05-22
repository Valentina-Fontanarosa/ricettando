package com.ricette.demo.repository;

import com.ricette.demo.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

    boolean existsByNameAndSurnameAndDateOfBirth(String name, String surname, LocalDate birthdate);

    @Query("SELECT u, COUNT(r) " +
            "FROM User u " +
            "LEFT JOIN u.recipes r " +
            "GROUP BY u")
    List<User> getUsersWithRecipeCount();


    @Query("SELECT c.user FROM Credentials c WHERE c.ruolo = 'GENERIC_USER'")
    List<User> findAllByCredentials_Ruolo();

}