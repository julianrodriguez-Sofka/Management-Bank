package com.Bank.Management.controller;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.entity.User;
import com.Bank.Management.mapper.UserMapper; // Asegúrate de tener esta importación
import com.Bank.Management.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper; // Declara la variable para el mapper

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        User registeredUser = userService.registerUser(userRegistrationDto);
        UserResponseDto responseDto = userMapper.toUserResponseDto(registeredUser);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}