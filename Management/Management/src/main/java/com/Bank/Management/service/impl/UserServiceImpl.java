package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.UpdateUserDTO;
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
    public UserResponseDto registerUser(UserRegistrationDto userRegistrationDto) {
        if (userRepository.findByUsername(userRegistrationDto.getUsername()).isPresent() ||
                userRepository.findByEmail(userRegistrationDto.getEmail()).isPresent()) {
            throw new RuntimeException("El nombre de usuario o email ya están en uso.");
        }
        User user = userMapper.toUser(userRegistrationDto);
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return userMapper.toUserResponseDto(user);
    }

    @Override
    public UserResponseDto update(UpdateUserDTO updateUserDTO) {
        // Busca al usuario por su ID y lanza una excepción si no se encuentra.
        User userToUpdate = userRepository.findById(updateUserDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + updateUserDTO.getId()));

        // Usa el mapper para actualizar los campos de la entidad con los datos del DTO.
        userMapper.updateUserFromDto(updateUserDTO, userToUpdate);

        // Guarda y devuelve el usuario actualizado.
        User updatedUser = userRepository.save(userToUpdate);
        return userMapper.toUserResponseDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        // Verifica si el usuario existe antes de intentar eliminarlo.
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + id + ". La eliminación no fue posible.");
        }
    }
}