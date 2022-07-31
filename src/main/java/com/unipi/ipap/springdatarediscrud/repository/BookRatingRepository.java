package com.unipi.ipap.springdatarediscrud.repository;

import com.unipi.ipap.springdatarediscrud.entity.BookRating;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRatingRepository extends CrudRepository<BookRating, String> {
}
