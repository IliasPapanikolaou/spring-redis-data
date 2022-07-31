package com.unipi.ipap.springdatarediscrud.controller;

import com.unipi.ipap.springdatarediscrud.entity.Cart;
import com.unipi.ipap.springdatarediscrud.entity.CartItem;
import com.unipi.ipap.springdatarediscrud.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{id}")
    public Cart get(@PathVariable("id") String id) {
        return cartService.get(id);
    }

    @PostMapping("/{id}")
    public void addToCart(@PathVariable("id") String id, @RequestBody CartItem item) {
        cartService.addToCart(id, item);
    }

    @DeleteMapping("/{id}")
    public void removeFromCart(@PathVariable("id") String id, @RequestBody String isbn) {
        cartService.removeFromCart(id, isbn);
    }

    @PostMapping("/{id}/checkout")
    public void checkout(@PathVariable("id") String id) {
        cartService.checkout(id);
    }

}
