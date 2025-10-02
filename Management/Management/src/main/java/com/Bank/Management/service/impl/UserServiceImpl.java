package com.Bank.Management.service.impl;


import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.DuplicatedDataException;
import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.request.UpdateUserDTO;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.entity.User;
import com.Bank.Management.mapper.UserMapper;
import com.Bank.Management.repository.UserRepository;
import com.Bank.Management.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto registerUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsByDni(userRegistrationDto.getDni())) {
            throw new DuplicatedDataException("DNI", userRegistrationDto.getDni());
        }

        if (userRepository.findByEmail(userRegistrationDto.getEmail()).isPresent()) {
            throw new DuplicatedDataException("Email", userRegistrationDto.getEmail());
        }

        User user = userMapper.toUser(userRegistrationDto);

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id, "Usuario"));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto update(UpdateUserDTO updateUserDTO) {
        User userToUpdate = userRepository.findById(updateUserDTO.getId())
                .orElseThrow(() -> new DataNotFoundException(updateUserDTO.getId(), "Usuario para actualizar"));

        userMapper.updateUserFromDto(updateUserDTO, userToUpdate);

        User updatedUser = userRepository.save(userToUpdate);
        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new DataNotFoundException(id, "Usuario");
        }
        userRepository.deleteById(id);
    }
}