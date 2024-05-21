package com.ricette.demo.repository;

import com.ricette.demo.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    boolean existsByTitle(String title);

    List<Category> findAll();
}
