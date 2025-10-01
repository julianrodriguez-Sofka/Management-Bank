package com.Bank.Management.controller;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.AccountOperationDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;

import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.InsufficientFundsException;
import com.Bank.Management.exception.GlobalExceptionHandler;
import com.Bank.Management.service.BankAccountService;

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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BankAccountService bankAccountService;

    private BankAccountController bankAccountController;

    private ObjectMapper objectMapper;
    private final Long TEST_ID = 10L;
    private final Long NON_EXISTENT_ID = 99L;
    private final String TEST_ACCOUNT_NUM = "456789";
    private final double INITIAL_BALANCE = 1000.00;
    private final double OPERATION_AMOUNT_DOUBLE = 100.00;
    private final Long DEFAULT_ACCOUNT_TYPE_ID = 1L;
    private BankAccountRequestDto createAccountDto;
    private BankAccountResponseDto accountResponseDto;
    private AccountOperationDto operationDto;

    @BeforeEach
    void setUp() {
        bankAccountController = new BankAccountController(bankAccountService);
        mockMvc = MockMvcBuilders.standaloneSetup(bankAccountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        createAccountDto = new BankAccountRequestDto(1L, DEFAULT_ACCOUNT_TYPE_ID);
        accountResponseDto = new BankAccountResponseDto();
        accountResponseDto.setId(TEST_ID);
        accountResponseDto.setAccountNumber(TEST_ACCOUNT_NUM);
        accountResponseDto.setBalance(INITIAL_BALANCE);
        operationDto = new AccountOperationDto(TEST_ACCOUNT_NUM, BigDecimal.valueOf(OPERATION_AMOUNT_DOUBLE));
    }

    //Objetivo: Crear una cuenta exitosamente (POST /api/accounts)
    @Test
    void createAccount_Success() throws Exception {
        // 1. Datos de entrada/salida simulada (definidos en setUp)

        // 2. Establecer comportamientos simulados
        Mockito.when(bankAccountService.createAccount(any(BankAccountRequestDto.class))).thenReturn(accountResponseDto);

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountDto)))
                .andExpect(status().isCreated()) // Espera 201 Created
                .andExpect(jsonPath("$.accountNumber").value(TEST_ACCOUNT_NUM))
                .andExpect(jsonPath("$.balance").value(INITIAL_BALANCE));

        // 5. Verificar interacciones
        verify(bankAccountService).createAccount(any(BankAccountRequestDto.class));
    }

    //Objetivo: Realizar un depósito exitosamente (PUT /api/accounts/deposit)
    @Test
    void deposit_Success() throws Exception {
        // 1. Datos de salida simulados (balance después del depósito)
        double newBalance = INITIAL_BALANCE + OPERATION_AMOUNT_DOUBLE;
        BankAccountResponseDto updatedAccount = new BankAccountResponseDto();
        updatedAccount.setBalance(newBalance);

        // 2. Establecer comportamientos simulados
        Mockito.when(bankAccountService.deposit(any(AccountOperationDto.class))).thenReturn(updatedAccount);

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(put("/api/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operationDto)))
                .andExpect(status().isOk()) // Espera 200 OK
                .andExpect(jsonPath("$.balance").value(newBalance));

        // 5. Verificar interacciones
        verify(bankAccountService).deposit(any(AccountOperationDto.class));
    }

    // Objetivo: Fallo al retirar por fondos insuficientes (InsufficientFundsException)
    @Test
    void withdraw_Fails_InsufficientFunds() throws Exception {
        // 2. Establecer comportamientos simulados (Lanza excepción de negocio)
        Mockito.when(bankAccountService.withdraw(any(AccountOperationDto.class)))
                .thenThrow(new InsufficientFundsException("Saldo insuficiente"));

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(put("/api/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(operationDto)))
                .andExpect(status().isBadRequest()); // Asume 400 Bad Request/409 Conflict

        // 5. Verificar interacciones
        verify(bankAccountService).withdraw(any(AccountOperationDto.class));
    }

    //Objetivo: Fallo al obtener una cuenta por ID no encontrado (DataNotFoundException)
    @Test
    void getAccountById_Fails_NotFound() throws Exception {
        // 2. Establecer comportamientos simulados (Lanza excepción 404)
        Mockito.when(bankAccountService.getAccountById(NON_EXISTENT_ID))
                .thenThrow(new DataNotFoundException(NON_EXISTENT_ID, "Cuenta Bancaria"));

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(get("/api/accounts/{id}", NON_EXISTENT_ID))
                .andExpect(status().isNotFound()); // Espera 404 Not Found

        // 5. Verificar interacciones
        verify(bankAccountService).getAccountById(NON_EXISTENT_ID);
    }

    @Test void getAllAccounts() {}
    @Test void getAccountById() {}
    @Test void updateAccount() {}
    @Test void deleteAccount() {}
    @Test void withdraw() {}
    @Test void deposit() {}
    @Test void createAccount() {}
}