package com.Bank.Management.service;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.entity.User;

import java.util.List;

public interface UserService {
    User registerUser(UserRegistrationDto registrationDto);
    List<User> getAllUsers();
}