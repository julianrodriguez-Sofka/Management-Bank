package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.entity.User;
import com.Bank.Management.mapper.UserMapper;
import com.Bank.Management.repository.UserRepository;
import com.Bank.Management.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User registerUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.findByUsername(userRegistrationDto.getUsername()).isPresent() ||
                userRepository.findByEmail(userRegistrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("El nombre de usuario o email ya están en uso.");
        }

        User user = userMapper.toUser(userRegistrationDto);
        return userRepository.save(user);
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseDtoList(users);
    }
}