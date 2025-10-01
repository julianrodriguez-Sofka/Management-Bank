package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.request.UpdateUserDTO;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.User;
import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.DuplicatedDataException;
import com.Bank.Management.mapper.UserMapper;
import com.Bank.Management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private User userTest;
    private UserRegistrationDto registerDto;
    private UserResponseDto responseDto;
    private final String TEST_EMAIL = "test@bank.com";
    private final Long TEST_ID = 1L;

    @BeforeEach
    void setUp() {
        userServiceImpl = new UserServiceImpl(userRepository, userMapper);

        userTest = new User();
        userTest.setId(TEST_ID);
        userTest.setUsername("TestUser");
        userTest.setEmail(TEST_EMAIL);
        userTest.setPassword("hashed_password");

        registerDto = new UserRegistrationDto(
                "TestUser",
                TEST_EMAIL,
                "securePwd123"
        );

        responseDto = new UserResponseDto(
                TEST_ID,
                "TestUser",
                TEST_EMAIL,
                "CUSTOMER",
                new ArrayList<BankAccountResponseDto>());
    }


    //Objetivo: Registro de Usuario (registerUser) - Caso de Éxito

    @Test
    void registerUser_Success_ReturnsSavedUser() {
        // 2. Establecer comportamientos simulados
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userMapper.toUser(any(UserRegistrationDto.class))).thenReturn(userTest);
        when(userRepository.save(any(User.class))).thenReturn(userTest);
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDto);

        // 3. Llamar al metodo a probar
        var result = userServiceImpl.registerUser(registerDto);

        // 4. Verificar los resultados
        assertAll("User registration successful",
                () -> assertInstanceOf(UserResponseDto.class, result),
                () -> assertEquals(TEST_ID, result.getId())
        );

        // 5. Verificar interacciones
        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(userRepository).save(userTest);
        verify(userMapper).toUser(registerDto);
    }


    //Objetivo: Registro de Usuario (registerUser) - Caso de Fallo (Duplicado)

    @Test
    void registerUser_Fails_ThrowsDuplicatedDataException() {
        // 2. Establecer comportamientos simulados
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(userTest));

        // 3. Llamar al metodo y esperar la excepción
        assertThrows(DuplicatedDataException.class, () -> userServiceImpl.registerUser(registerDto));

        // 5. Verificar interacciones
        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }


    //Objetivo: Obtener Usuario por ID (getUserById) - Caso de Éxito

    @Test
    void getUserById_Success_ReturnsUser() {
        // 2. Establecer comportamientos simulados
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(userTest));
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDto);

        // 3. Llamar al metodo a probar
        var result = userServiceImpl.getUserById(TEST_ID);

        // 4. Verificar los resultados
        assertNotNull(result);

        // 5. Verificar interacciones
        verify(userRepository).findById(TEST_ID);
        verify(userMapper).toUserResponseDto(userTest);
    }

    // Objetivo:Obtener Usuario por ID (getUserById) - Caso de Fallo (No encontrado)

    @Test
    void getUserById_Fails_ThrowsDataNotFoundException() {
        // 2. Establecer comportamientos simulados
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        // 3. Llamar al metodo y esperar la excepcion
        assertThrows(DataNotFoundException.class, () -> userServiceImpl.getUserById(TEST_ID));

        // 5. Verificar interacciones
        verify(userRepository).findById(TEST_ID);
        verifyNoInteractions(userMapper);
    }

    // Objetivo: Obtener Todos los Usuarios (getAllUsers) - Caso de Éxito
    @Test
    void getAllUsers_Success_ReturnsListOfUsers() {
        List<User> userList = List.of(userTest);
        when(userRepository.findAll()).thenReturn(userList);
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDto);
        List<UserResponseDto> result = userServiceImpl.getAllUsers();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(TEST_ID, result.get(0).getId());
        verify(userRepository).findAll();
        verify(userMapper, times(userList.size())).toUserResponseDto(any(User.class));
    }

    // Objetivo: Actualizar Usuario (update) - Caso de Éxito
    @Test
    void update_Success_ReturnsUpdatedUser() {
        UpdateUserDTO updateDto = new UpdateUserDTO(TEST_ID, "NewName", "new@bank.com", "NewPassword");
        User updatedUserEntity = new User();
        updatedUserEntity.setId(TEST_ID);
        updatedUserEntity.setUsername("NewName");
        updatedUserEntity.setEmail("new@bank.com");
        updatedUserEntity.setPassword("NewPassword");

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(userTest));
        when(userRepository.save(any(User.class))).thenReturn(updatedUserEntity);
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDto);

        UserResponseDto result = userServiceImpl.update(updateDto);

        assertNotNull(result);

        verify(userRepository).findById(TEST_ID);
        verify(userMapper).updateUserFromDto(updateDto, userTest);
        verify(userRepository).save(userTest);
        verify(userMapper).toUserResponseDto(updatedUserEntity);
    }

    // Objetivo: Actualizar Usuario (update) - Caso de Fallo (No encontrado)
    @Test
    void update_Fails_ThrowsDataNotFoundException() {
        UpdateUserDTO updateDto = new UpdateUserDTO(TEST_ID, "NewName", "new@bank.com", "NewPassword");

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userServiceImpl.update(updateDto));

        verify(userRepository).findById(TEST_ID);
        verify(userRepository, never()).save(any());
        verifyNoMoreInteractions(userMapper);
    }

    // Objetivo: Eliminar Usuario (delete) - Caso de Éxito
    @Test
    void delete_Success_PerformsDeletion() {
        when(userRepository.existsById(TEST_ID)).thenReturn(true);
        doNothing().when(userRepository).deleteById(TEST_ID);

        userServiceImpl.delete(TEST_ID);

        verify(userRepository).existsById(TEST_ID);
        verify(userRepository).deleteById(TEST_ID);
    }

    // Objetivo: Eliminar Usuario (delete) - Caso de Fallo (No encontrado)
    @Test
    void delete_Fails_ThrowsDataNotFoundException() {
        when(userRepository.existsById(TEST_ID)).thenReturn(false);

        assertThrows(DataNotFoundException.class, () -> userServiceImpl.delete(TEST_ID));

        verify(userRepository).existsById(TEST_ID);
        verify(userRepository, never()).deleteById(any());
    }
}