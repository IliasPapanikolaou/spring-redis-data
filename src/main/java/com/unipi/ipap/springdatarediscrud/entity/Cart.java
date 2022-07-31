package com.unipi.ipap.springdatarediscrud.entity;

import java.util.Set;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Cart {
    private String id;
    private String userId;

    /* @Singular annotation provides a convenient way of working with the List, Set, and Map
    interfaces using the Builder pattern. */
    @Singular
    private Set<CartItem> cartItems;

    public Integer count() {
        return getCartItems().size();
    }

    public Double getTotal() {
        return cartItems
                .stream()
                .mapToDouble(ci -> ci.getPrice() * ci.getQuantity())
                .sum();
    }
}