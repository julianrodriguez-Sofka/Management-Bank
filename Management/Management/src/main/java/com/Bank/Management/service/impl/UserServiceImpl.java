package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.entity.User;
import com.Bank.Management.mapper.UserMapper;
import com.Bank.Management.repository.UserRepository;
import com.Bank.Management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User registerUser(UserRegistrationDto registrationDto) {
        // Validación: verificar si el nombre de usuario o email ya existen.
        Optional<User> existingUserByUsername = userRepository.findByUsername(registrationDto.getUsername());
        Optional<User> existingUserByEmail = userRepository.findByEmail(registrationDto.getEmail());

        if (existingUserByUsername.isPresent() || existingUserByEmail.isPresent()) {
            throw new RuntimeException("El nombre de usuario o email ya están en uso.");
        }

        // Usar MapStruct para mapear el DTO a la entidad User
        User user = userMapper.toUser(registrationDto);
        user.setAccounts(new HashSet<>());

        // Guardar el nuevo usuario en la base de datos
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

}