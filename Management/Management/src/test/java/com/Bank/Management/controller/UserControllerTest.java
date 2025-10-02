package com.Bank.Management.controller;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.request.UpdateUserDTO;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.DuplicatedDataException;
import com.Bank.Management.exception.GlobalExceptionHandler;
import com.Bank.Management.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private UserController userController;

    private ObjectMapper objectMapper;

    private UserRegistrationDto registerDto;
    private UpdateUserDTO updateDto;
    private UserResponseDto responseDto;

    private final Long TEST_ID = 1L;
    private final Long NON_EXISTENT_ID = 99L;

    private final String TEST_DNI = "12345678X";
    private final String TEST_EMAIL = "test@bank.com";
    private final String TEST_USERNAME = "TestUser";

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        registerDto = new UserRegistrationDto(TEST_DNI, TEST_USERNAME, TEST_EMAIL, "securePwd123");
        responseDto = new UserResponseDto(TEST_ID, TEST_DNI, TEST_USERNAME, TEST_EMAIL, "CUSTOMER", new ArrayList<>());
        updateDto = new UpdateUserDTO(TEST_ID, "NewName", TEST_EMAIL, "NewPwd123");
    }

    // 1. Objetivo: Registrar un usuario exitosamente (POST /api/users/register)
    @Test
    void registerUser_Success() throws Exception {
        // 2. Establecer comportamientos simulados
        Mockito.when(userService.registerUser(Mockito.any(UserRegistrationDto.class))).thenReturn(responseDto);

        // 3. y 4. Llamar al método a probar y verificar
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated()) // Espera 201 Created
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.dni").value(TEST_DNI)); // 🛑 Verificar el DNI

        // 5. Verificar interacciones
        Mockito.verify(userService).registerUser(Mockito.any(UserRegistrationDto.class));
    }
    @Test
    void registerUser_Fails_DniDuplicated() throws Exception {
        // 2. Establecer comportamientos simulados
        Mockito.when(userService.registerUser(Mockito.any(UserRegistrationDto.class)))
                .thenThrow(new DuplicatedDataException("DNI", TEST_DNI));

        // 3. y 4. Llamar al método a probar y verificar
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El DNI '" + TEST_DNI + "' ya existe y debe ser único."));

        // 5. Verificar interacciones
        verify(userService).registerUser(Mockito.any(UserRegistrationDto.class));
    }

    // Objetivo: Actualizar un usuario exitosamente (PUT /api/users/update)
    @Test
    void updateUser_Success() throws Exception {
        // 1. Datos de salida simulados después de la actualización
        UserResponseDto updatedUserDto = new UserResponseDto(TEST_ID, TEST_DNI, "NewName", TEST_EMAIL, "CUSTOMER", new ArrayList<>());

        // 2. Establecer comportamientos simulados
        Mockito.when(userService.update(Mockito.any(UpdateUserDTO.class))).thenReturn(updatedUserDto);

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // Espera 200 OK
                .andExpect(jsonPath("$.id").value(TEST_ID))
                .andExpect(jsonPath("$.username").value("NewName"));

        // 5. Verificar interacciones
        Mockito.verify(userService).update(Mockito.any(UpdateUserDTO.class));
    }

    //Objetivo: Fallo al registrar por datos inválidos (Email/DNI vacío o mal formato)
    @Test
    void registerUser_InvalidData() throws Exception {
        // 1. Datos de entrada inválidos (DTO con DNI vacío)
        UserRegistrationDto invalidDto = new UserRegistrationDto("", "Invalido", TEST_EMAIL, "pass");

        // 2. Comportamiento simulado: Falla por validación del Controller.

        // 3. y 4. Llamar al metodo a probar y verificar (Esperar 400 Bad Request)
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); // Espera 400 Bad Request

        // 5. Verificar interacciones (El servicio NO debe ser llamado)
        Mockito.verifyNoInteractions(userService);
    }

    //Objetivo: Eliminar un usuario exitosamente (DELETE /api/users/{id})
    @Test
    void deleteUser_Success() throws Exception {
        // 2. Establecer comportamientos simulados: el servicio no devuelve nada (void)
        Mockito.doNothing().when(userService).delete(TEST_ID);

        // 3. y 4. Llamar al metodo a probar y verificar (Esperar 204 NO_CONTENT)
        mockMvc.perform(delete("/api/users/{id}", TEST_ID))
                .andExpect(status().isNoContent())
                .andExpect(content().string("")); // El cuerpo debe estar vacío para 204

        // 5. Verificar interacciones
        Mockito.verify(userService).delete(TEST_ID);
    }


    @Test
    void registerUser() {}
    @Test
    void getAllUsers() {}
    @Test
    void getUserById() {}
    @Test
    void updateUser() {}
}