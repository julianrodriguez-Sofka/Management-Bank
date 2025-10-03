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
    private final String TEST_DNI = "12345678X";
    private final String NEW_DNI = "98765432Y";

    @BeforeEach
    void setUp() {
        userServiceImpl = new UserServiceImpl(userRepository, userMapper);

        userTest = new User();
        userTest.setId(TEST_ID);
        userTest.setUsername("TestUser");
        userTest.setEmail(TEST_EMAIL);
        userTest.setPassword("hashed_password");
        userTest.setDni(TEST_DNI);

        registerDto = new UserRegistrationDto(
                TEST_DNI,
                "TestUser",
                TEST_EMAIL,
                "securePwd123"
        );

        responseDto = new UserResponseDto(
                TEST_ID,
                TEST_DNI,
                "TestUser",
                TEST_EMAIL,
                "CUSTOMER",
                new ArrayList<BankAccountResponseDto>());
    }


    //Objetivo: Registro de Usuario (registerUser) - Caso de Éxito

    @Test
    void registerUser_Success_ReturnsSavedUser() {
        when(userRepository.existsByDni(registerDto.getDni())).thenReturn(false);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userMapper.toUser(any(UserRegistrationDto.class))).thenReturn(userTest);
        when(userRepository.save(any(User.class))).thenReturn(userTest);
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDto);

        var result = userServiceImpl.registerUser(registerDto);

        assertAll("User registration successful",
                () -> assertInstanceOf(UserResponseDto.class, result),
                () -> assertEquals(TEST_ID, result.getId())
        );

        verify(userRepository).existsByDni(registerDto.getDni());
        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(userRepository).save(userTest);
        verify(userMapper).toUser(registerDto);
    }


    //Objetivo: Registro de Usuario (registerUser) - Caso de Fallo (Duplicado Email)

    @Test
    void registerUser_Fails_ThrowsDuplicatedDataException() {
        when(userRepository.existsByDni(registerDto.getDni())).thenReturn(false);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(userTest));

        assertThrows(DuplicatedDataException.class, () -> userServiceImpl.registerUser(registerDto));

        verify(userRepository).existsByDni(registerDto.getDni());
        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    //Objetivo: Registro de Usuario (registerUser) - Caso de Fallo (Duplicado DNI)

    @Test
    void registerUser_Fails_ThrowsDuplicatedDniException() {
        when(userRepository.existsByDni(registerDto.getDni())).thenReturn(true);

        assertThrows(DuplicatedDataException.class, () -> userServiceImpl.registerUser(registerDto));

        verify(userRepository).existsByDni(registerDto.getDni());
        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }


    //Objetivo: Obtener Usuario por ID (getUserById) - Caso de Éxito

    @Test
    void getUserById_Success_ReturnsUser() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(userTest));
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDto);

        var result = userServiceImpl.getUserById(TEST_ID);

        assertNotNull(result);

        verify(userRepository).findById(TEST_ID);
        verify(userMapper).toUserResponseDto(userTest);
    }

    // Objetivo:Obtener Usuario por ID (getUserById) - Caso de Fallo (No encontrado)

    @Test
    void getUserById_Fails_ThrowsDataNotFoundException() {
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> userServiceImpl.getUserById(TEST_ID));

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
        // Creamos un DTO con el DNI existente para probar que NO haya validación si el DNI no cambia
        UpdateUserDTO updateDto = new UpdateUserDTO(TEST_ID, TEST_DNI, "NewName", "new@bank.com", "NewPassword");
        User updatedUserEntity = new User();
        updatedUserEntity.setId(TEST_ID);
        updatedUserEntity.setUsername("NewName");
        updatedUserEntity.setEmail("new@bank.com");
        updatedUserEntity.setPassword("NewPassword");
        updatedUserEntity.setDni(TEST_DNI);

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(userTest));
        when(userRepository.save(any(User.class))).thenReturn(updatedUserEntity);
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDto);

        // Mockeamos el mapper para actualizar el DNI en la entidad para el save
        doAnswer(invocation -> {
            User user = invocation.getArgument(1);
            user.setDni(TEST_DNI);
            user.setUsername("NewName");
            return null;
        }).when(userMapper).updateUserFromDto(any(UpdateUserDTO.class), eq(userTest));

        UserResponseDto result = userServiceImpl.update(updateDto);

        assertNotNull(result);

        verify(userRepository).findById(TEST_ID);
        verify(userMapper).updateUserFromDto(updateDto, userTest);
        verify(userRepository, never()).existsByDni(anyString()); // 👈 Verifica que la validación no se llamó
        verify(userRepository).save(userTest);
        verify(userMapper).toUserResponseDto(updatedUserEntity);
    }

    // Objetivo: Actualizar Usuario (update) - Caso de Éxito (Modificación del DNI)
    @Test
    void update_Success_DniModifiedAndValidated() {
        UpdateUserDTO updateDto = new UpdateUserDTO(TEST_ID, NEW_DNI, "NewName", "new@bank.com", "NewPassword");

        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(userTest));

        // Mockeamos la validación: El nuevo DNI NO existe en la base de datos (es único)
        when(userRepository.existsByDni(NEW_DNI)).thenReturn(false);

        when(userRepository.save(any(User.class))).thenReturn(userTest);
        when(userMapper.toUserResponseDto(any(User.class))).thenReturn(responseDto);

        // Llamada al metodo
        userServiceImpl.update(updateDto);

        // Verificaciones
        verify(userRepository).findById(TEST_ID);
        verify(userRepository).existsByDni(NEW_DNI);
        verify(userMapper).updateUserFromDto(updateDto, userTest);
        verify(userRepository).save(userTest);
    }

    // Objetivo: Actualizar Usuario (update) - Caso de Fallo (DNI Duplicado por otro usuario)
    @Test
    void update_Fails_ThrowsDuplicatedDataExceptionForDni() {
        final String CONFLICTING_DNI = "11111111Z";
        UpdateUserDTO updateDto = new UpdateUserDTO(TEST_ID, CONFLICTING_DNI, "NewName", "new@bank.com", "NewPassword");

        // 1. El usuario a actualizar es encontrado
        when(userRepository.findById(TEST_ID)).thenReturn(Optional.of(userTest));

        // 2. Mockeamos la validación: El nuevo DNI SÍ existe en la base de datos (Conflicto)
        when(userRepository.existsByDni(CONFLICTING_DNI)).thenReturn(true);

        // Verificamos que se lance la excepción correcta
        assertThrows(DuplicatedDataException.class, () -> userServiceImpl.update(updateDto));

        // Verificaciones
        verify(userRepository).findById(TEST_ID);
        verify(userRepository).existsByDni(CONFLICTING_DNI);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userRepository, never()).save(any());
    }

    // Objetivo: Actualizar Usuario (update) - Caso de Fallo (No encontrado)
    @Test
    void update_Fails_ThrowsDataNotFoundException() {
        UpdateUserDTO updateDto = new UpdateUserDTO(TEST_ID, TEST_DNI, "NewName", "new@bank.com", "NewPassword");

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