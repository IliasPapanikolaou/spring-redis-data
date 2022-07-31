package com.unipi.ipap.springdatarediscrud.service;

import com.redislabs.modules.rejson.JReJSON;
import com.redislabs.modules.rejson.Path;
import com.unipi.ipap.springdatarediscrud.entity.AppUser;
import com.unipi.ipap.springdatarediscrud.entity.Book;
import com.unipi.ipap.springdatarediscrud.entity.Cart;
import com.unipi.ipap.springdatarediscrud.entity.CartItem;
import com.unipi.ipap.springdatarediscrud.repository.AppUserRepository;
import com.unipi.ipap.springdatarediscrud.repository.BookRepository;
import com.unipi.ipap.springdatarediscrud.repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.stream.LongStream;

@Slf4j
@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AppUserRepository userRepository;

    private JReJSON redisJson = new JReJSON();

    Path cartItemPath = Path.of(".cartItems");

    public Cart get(String id) {
        return cartRepository.findById(id).orElse(null);
    }

    public void addToCart(String id, CartItem item) {
        Optional<Book> book = bookRepository.findById(item.getIsbn());
        if (book.isPresent()) {
            String cartKey = CartRepository.getKey(id);
            item.setPrice(book.get().getPrice());
            redisJson.arrAppend(cartKey, cartItemPath, item);
        }
    }

    public void removeFromCart(String id, String isbn) {
        Optional<Cart> cartFinder = cartRepository.findById(id);
        if (cartFinder.isPresent()) {
            Cart cart = cartFinder.get();
            String cartKey = CartRepository.getKey(cart.getId());
            List<CartItem> cartItems = new ArrayList<>(cart.getCartItems());
            OptionalLong cartItemIndex = LongStream.range(0, cartItems.size())
                    .filter(i -> cartItems.get((int) i).getIsbn().equals(isbn)).findFirst();
            if (cartItemIndex.isPresent()) {
                redisJson.arrPop(cartKey, CartItem.class, cartItemPath, cartItemIndex.getAsLong());
            }
        }
    }

    public void checkout(String id) {
        try {
            Cart cart = cartRepository.findById(id).orElseThrow(ChangeSetPersister.NotFoundException::new);
            AppUser user = userRepository.findById(cart.getUserId())
                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
            cart.getCartItems().forEach(cartItem -> {
                Book book;
                try {
                    book = bookRepository.findById(cartItem.getIsbn())
                            .orElseThrow(ChangeSetPersister.NotFoundException::new);
                    user.addBook(book);
                } catch (ChangeSetPersister.NotFoundException e) {
                    log.warn("Book not found", e);
                }
                userRepository.save(user);
            });
        } catch (ChangeSetPersister.NotFoundException e) {
            log.warn("Cart or User not found", e);
        }
    }
}
