package com.unipi.ipap.springdatarediscrud.repository;

import com.unipi.ipap.springdatarediscrud.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {
    Optional<Product> findFirstById(String id);

    Product findFirstByProductId(Long id);
}
