package com.Bank.Management.repository;

import com.Bank.Management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);

    boolean existsByDni(String dni);
}

// S: Contener la logica de persistencia de los usuarios (DB)
// Si cambio de DB, solo cambio el repositorio y sus drivers sin afectar UserServiceImpl
