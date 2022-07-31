package com.unipi.ipap.springdatarediscrud.data;

import com.unipi.ipap.springdatarediscrud.entity.AppUser;
import com.unipi.ipap.springdatarediscrud.entity.Book;
import com.unipi.ipap.springdatarediscrud.entity.BookRating;
import com.unipi.ipap.springdatarediscrud.repository.BookRatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.stream.IntStream;

@Component
@Order(4)
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "generateData")
public class CreateBookRatings implements CommandLineRunner {

    @Value("${app.numberOfRatings}")
    private Integer numberOfRatings;

    @Value("${app.ratingStars}")
    private Integer ratingStars;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BookRatingRepository bookRatingRepo;

    @Override
    public void run(String... args) throws Exception {
        if (bookRatingRepo.count() == 0) {
            Random random = new Random();
            IntStream.range(0, numberOfRatings).forEach(n -> {
                String bookId = redisTemplate.opsForSet().randomMember(Book.class.getName());
                String userId = redisTemplate.opsForSet().randomMember(AppUser.class.getName());
                int stars = random.nextInt(ratingStars) + 1;

                AppUser user = new AppUser();
                user.setId(userId);

                Book book = new Book();
                book.setId(bookId);

                BookRating rating = BookRating.builder()
                        .user(user)
                        .book(book)
                        .rating(stars)
                        .build();
                bookRatingRepo.save(rating);
            });
            log.info(">>>> BookRating created...");
        }
    }
}
