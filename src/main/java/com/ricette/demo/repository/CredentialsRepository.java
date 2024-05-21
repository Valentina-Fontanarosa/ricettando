package com.ricette.demo.repository;

import com.ricette.demo.model.Credentials;
import com.ricette.demo.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {
    Optional<Credentials> findByUsername(String username);


    Credentials findByUser(User user);
}
