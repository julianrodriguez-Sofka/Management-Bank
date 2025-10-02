package com.Bank.Management.service;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.request.UpdateUserDTO;
import com.Bank.Management.dto.response.UserResponseDto;
import java.util.List;

public interface UserService {


    UserResponseDto registerUser(UserRegistrationDto userRegistrationDto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    UserResponseDto update(UpdateUserDTO updateUserDTO);
    void delete(Long id);
}

// I: Se cumple al tener interfaces segregades, mi UserController solo inyecta la interfaz UserService
// No estoy forzando a mi UserController a depender de la TransactionService o BankAccountService, que no utiliza.
// Si la interfaz de Transacciones cambia, no afecta a UserController.