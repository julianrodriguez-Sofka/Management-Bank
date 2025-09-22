package com.Bank.Management.controller;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.entity.User;
import com.Bank.Management.mapper.UserMapper;
import com.Bank.Management.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        User registeredUser = userService.registerUser(userRegistrationDto); // Recibe la entidad 'User'
        UserResponseDto responseDto = userMapper.toUserResponseDto(registeredUser); // Mapea a DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}