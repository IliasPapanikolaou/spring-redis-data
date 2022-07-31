package com.unipi.ipap.springdatarediscrud.repository;

import com.unipi.ipap.springdatarediscrud.entity.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, String> {
}
