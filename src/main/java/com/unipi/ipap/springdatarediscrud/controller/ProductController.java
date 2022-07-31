package com.unipi.ipap.springdatarediscrud.controller;

import com.unipi.ipap.springdatarediscrud.entity.Cart;
import com.unipi.ipap.springdatarediscrud.entity.Product;
import com.unipi.ipap.springdatarediscrud.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CachePut(value = "product-cache", key = "#product.id")
    public Product save(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    @Cacheable(value = "product-cache", key = "#id", unless="#result == null")
    public Product findProduct(@PathVariable String id) {
        return productRepository.findFirstById(id).orElse(null);
    }

    @GetMapping("/pid/{pid}")
    public Product findProductByProductId(@PathVariable long pid) {
        return productRepository.findFirstByProductId(pid);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "product-cache", key = "#id")
    public void remove(@PathVariable String id) {
        productRepository.deleteById(id);
    }

}
