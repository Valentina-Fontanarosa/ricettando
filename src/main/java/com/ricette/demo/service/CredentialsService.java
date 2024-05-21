package com.ricette.demo.service;

import com.ricette.demo.model.Category;
import com.ricette.demo.model.Credentials;
import com.ricette.demo.model.User;
import com.ricette.demo.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        if (credentials.getRuolo() == null) {
            credentials.setRuolo(Credentials.GENERIC_USER_ROLE);
        }
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return credentialsRepository.save(credentials);
    }

    @Transactional
    public Credentials save(Credentials credentials) {
        return credentialsRepository.save(credentials);
    }

    public Credentials findByUser(User user) {
        return this.credentialsRepository.findByUser(user);
    }

    public void delete(Credentials credentials) {
        this.credentialsRepository.delete(credentials);
    }
}