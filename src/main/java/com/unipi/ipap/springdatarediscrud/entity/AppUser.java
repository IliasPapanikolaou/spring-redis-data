package com.unipi.ipap.springdatarediscrud.entity;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Data
@RedisHash
public class AppUser {

    @Id
    @ToString.Include
    private String id;

    @NotNull
    @Size(min = 2, max = 48)
    @ToString.Include
    private String name;

    /*
     * @Indexed annotation can be used to create a secondary index. Secondary indexes enable lookup operations
     * based on native Redis structures. The index is maintained on every save/update of an indexed object.
     * To add a secondary index to the Role model, we’ll simply add the @Indexed annotation:
     */
    @NotNull
    @Email
    @EqualsAndHashCode.Include
    @ToString.Include
    @Indexed // Enables FindBy functionality
    private String email;

    @NotNull
    private String password;

    @Transient // @RedisHash does not attempt to serialize it to the database
    private String passwordConfirm;

    /*
     * We have a Set of Role objects marked as @References which will cause them to be stored as
     * the id of a given role in the Redis Hash backing a User instance.
     */
    @Reference
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Role> roles = new HashSet<>();

    @Reference
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Book> books = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
    }

    public void addBook(Book book) {
        books.add(book);
    }
}
