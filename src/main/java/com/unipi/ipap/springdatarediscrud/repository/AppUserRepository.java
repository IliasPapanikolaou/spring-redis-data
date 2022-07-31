package com.unipi.ipap.springdatarediscrud.repository;

import com.unipi.ipap.springdatarediscrud.entity.AppUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends CrudRepository<AppUser, String> {
    Optional<AppUser> findFirstByEmail(String email);
}
