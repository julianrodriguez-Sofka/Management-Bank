package com.Bank.Management.controller;

import com.Bank.Management.dto.request.UpdateUserDTO;
import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UserController {

    private final UserService userService;
// L: Se cumple porque el UserController espera un objeto del tipo
// UserService y Spring inyecta la implementación concreta UserServiceImpl.

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        UserResponseDto registeredUser = userService.registerUser(userRegistrationDto);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario por ID")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable @Valid Long id) {
        UserResponseDto user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar un usuario")
    public ResponseEntity<UserResponseDto> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserResponseDto updatedUser = userService.update(updateUserDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario por ID")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

// D: el módulo de alto nivel (UserController) no depende del módulo de bajo nivel (UserServiceImpl),
// sino de la abstracción (UserService). Esto invierte el flujo de control tradicional y asegura que nuestro código sea flexible,
// desacoplado y fácil de probar con mocks.