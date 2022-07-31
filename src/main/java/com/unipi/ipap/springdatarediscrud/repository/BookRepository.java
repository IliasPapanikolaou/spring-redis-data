package com.unipi.ipap.springdatarediscrud.repository;

import com.unipi.ipap.springdatarediscrud.entity.Book;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends PagingAndSortingRepository<Book, String> {
}
