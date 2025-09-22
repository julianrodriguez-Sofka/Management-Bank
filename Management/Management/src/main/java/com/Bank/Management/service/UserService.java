package com.Bank.Management.service;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.entity.User;
import java.util.List;

public interface UserService {
    // El método debe devolver una entidad 'User'
    User registerUser(UserRegistrationDto registrationDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long id);
}