package com.Bank.Management.repository;

import com.Bank.Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Encuentra un usuario por su nombre de usuario
    Optional<User> findByUsername(String username);

    // Encuentra un usuario por su email
    Optional<User> findByEmail(String email);
}
