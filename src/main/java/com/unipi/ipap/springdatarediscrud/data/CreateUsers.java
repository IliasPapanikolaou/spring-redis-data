package com.unipi.ipap.springdatarediscrud.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unipi.ipap.springdatarediscrud.entity.AppUser;
import com.unipi.ipap.springdatarediscrud.entity.Role;
import com.unipi.ipap.springdatarediscrud.repository.AppUserRepository;
import com.unipi.ipap.springdatarediscrud.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
@Order(2)
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "generateData")
public class CreateUsers implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.count() == 0) {
            // Load the roles
            Optional<Role> customerRoleOptional = roleRepository.findFirstByName("customer");
            Optional<Role> adminRoleOptional = roleRepository.findFirstByName("admin");

            try {
                Role customer = customerRoleOptional.orElseThrow(ChangeSetPersister.NotFoundException::new);

                // Create a Jackson object mapper
                ObjectMapper mapper = new ObjectMapper();
                // Create a type definition to convert the array of JSON into a List of Users
                TypeReference<List<AppUser>> typeReference = new TypeReference<>() { };
                // Make the JSON data available as an input stream
                InputStream inputStream = getClass().getResourceAsStream("/data/users/users.json");
                // Convert the JSON to objects
                List<AppUser> appUsers = mapper.readValue(inputStream, typeReference);
                // Encode Passwords, add role and save to redis
                appUsers.forEach(user -> {
                    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
                    user.addRole(customer);
                    userRepository.save(user);
                });
                log.info(">>>> " + appUsers.size() + " Users Saved!");
            } catch (ChangeSetPersister.NotFoundException | RuntimeException | IOException e) {
                log.warn(">>>> Unable to import users: " + e);
            }

            try {
                Role admin = adminRoleOptional.orElseThrow(ChangeSetPersister.NotFoundException::new);

                AppUser adminUser = new AppUser();
                adminUser.setName("Adminus Admistradore");
                adminUser.setEmail("admin@mail.com");
                adminUser.setPassword(bCryptPasswordEncoder.encode("Reindeer Flotilla"));
                adminUser.addRole(admin);
                // Save admin to redis
                userRepository.save(adminUser);
                log.info(">>>> Admin Saved!");
            } catch (ChangeSetPersister.NotFoundException e) {
                log.warn(">>>> Unable to import admin: " + e);
            }
        }
    }

}
