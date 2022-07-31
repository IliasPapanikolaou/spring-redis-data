package com.unipi.ipap.springdatarediscrud.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.validation.constraints.Size;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Product")
public class Product {
    @Id
    @Indexed
    @ToString.Include
    private String id;
    @ToString.Include
    @Indexed // Enables FindBy functionality
    private Long productId;
    @Size(min = 2, max = 48)
    @ToString.Include
    private String name;
    @ToString.Include
    private int quantity;
    @ToString.Include
    private double price;
}
