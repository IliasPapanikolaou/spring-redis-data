package com.unipi.ipap.springdatarediscrud.controller;

import com.redislabs.lettusearch.*;
import com.unipi.ipap.springdatarediscrud.entity.Book;
import com.unipi.ipap.springdatarediscrud.entity.Category;
import com.unipi.ipap.springdatarediscrud.repository.BookRepository;
import com.unipi.ipap.springdatarediscrud.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // RediSearch
    @Value("${app.booksSearchIndexName}")
    private String searchIndexName;
    @Autowired
    private StatefulRediSearchConnection<String, String> searchConnection;
    // RediSearch AutoCompletion
    @Value("${app.autoCompleteKey}")
    private String autoCompleteKey;

    // @GetMapping
    public Iterable<Book> all() {
        return bookRepository.findAll();
    }

    @GetMapping("/categories")
    public Iterable<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{isbn}")
    public Book get(@PathVariable("isbn") String isbn) {
        return bookRepository.findById(isbn).get();
    }

    // http://localhost:8080/api/books/?size=25&page=2
    @GetMapping
    public ResponseEntity<Map<String, Object>> all(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Pageable paging = PageRequest.of(page, size);
        Page<Book> pagedResult = bookRepository.findAll(paging);
        List<Book> books = pagedResult.hasContent() ? pagedResult.getContent() : Collections.emptyList();

        Map<String, Object> response = new HashMap<>();
        response.put("books", books);
        response.put("page", pagedResult.getNumber());
        response.put("pages", pagedResult.getTotalPages());
        response.put("total", pagedResult.getTotalElements());

        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
    }

    // RediSearch
    // http://localhost:8080/api/books/search/?q=%25scal%25
    // http://localhost:8080/api/books/search?q=java
    @Cacheable("book-search") // Spring will use Redis keys under the "book-search" prefix to store cache entries
    @GetMapping("/search")
    public SearchResults<String,String> search(@RequestParam(name="q")String query) {
        RediSearchCommands<String, String> commands = searchConnection.sync();
        SearchResults<String, String> results = commands.search(searchIndexName, query);
        return results;
    }

    // RediSearch AutoCompletion
    // http://localhost:8080/api/books/authors/?q=brian%20s
    // http://localhost:8080/api/books/authors/?q=brian%20sa
    // http://localhost:8080/api/books/authors/?q=Ven
    @GetMapping("/authors")
    public List<Suggestion<String>> authorAutoComplete(@RequestParam(name="q")String query) {
        RediSearchCommands<String, String> commands = searchConnection.sync();
        SuggetOptions options = SuggetOptions.builder().max(20L).build();
        return commands.sugget(autoCompleteKey, query, options);
    }
}
