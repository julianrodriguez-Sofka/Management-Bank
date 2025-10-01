package com.Bank.Management.controller;

import com.Bank.Management.dto.request.TransferRequestDto;
import com.Bank.Management.dto.response.TransactionResponseDto;
import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.InsufficientFundsException;
import com.Bank.Management.exception.GlobalExceptionHandler;
import com.Bank.Management.service.TransactionService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    private TransactionController transactionController;

    private ObjectMapper objectMapper;

    private final Long TEST_TRANSACTION_ID = 50L;
    private final Long NON_EXISTENT_ID = 99L;
    private final String SOURCE_ACCOUNT = "111111";
    private final String TARGET_ACCOUNT = "222222";
    private final String NON_EXISTENT_ACCOUNT = "000000";
    private final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(500.00);
    private final double TRANSFER_AMOUNT_DOUBLE = 500.00;
    private final String TRANSACTION_DESCRIPTION = "Transferencia de Prueba";

    private TransferRequestDto transferRequestDto;
    private TransactionResponseDto transactionResponseDto;
    private List<TransactionResponseDto> transactionList;

    @BeforeEach
    void setUp() {
        transactionController = new TransactionController(transactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        transferRequestDto = new TransferRequestDto(
                SOURCE_ACCOUNT,
                TARGET_ACCOUNT,
                TRANSFER_AMOUNT
        );

        transactionResponseDto = new TransactionResponseDto(
                TEST_TRANSACTION_ID,
                TRANSFER_AMOUNT_DOUBLE,
                LocalDateTime.now(),
                TRANSACTION_DESCRIPTION,
                SOURCE_ACCOUNT,
                TARGET_ACCOUNT
        );

        transactionList = Collections.singletonList(transactionResponseDto);
    }

    //Objetivo: Realizar una transferencia exitosamente (POST /api/transactions/transfer)
    @Test
    void transfer_Success() throws Exception {
        // 2. Establecer comportamientos simulados
        when(transactionService.transfer(any(TransferRequestDto.class))).thenReturn(transactionResponseDto);

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_TRANSACTION_ID.intValue()))
                .andExpect(jsonPath("$.targetAccountNumber").value(TARGET_ACCOUNT));

        // 5. Verificar interacciones
        verify(transactionService).transfer(any(TransferRequestDto.class));
    }

    //Objetivo: Obtener el historial de transacciones de una cuenta (GET /api/transactions/history/{accountNumber})
    @Test
    void getHistoryByAccountNumber_Success() throws Exception {
        // 2. Establecer comportamientos simulados
        when(transactionService.getHistoryByAccountNumber(eq(SOURCE_ACCOUNT))).thenReturn(transactionList);

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(get("/api/transactions/history/{accountNumber}", SOURCE_ACCOUNT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].sourceAccountNumber").value(SOURCE_ACCOUNT));

        // 5. Verificar interacciones
        verify(transactionService).getHistoryByAccountNumber(eq(SOURCE_ACCOUNT));
    }

    //Objetivo: Fallo en la transferencia por fondos insuficientes (InsufficientFundsException)
    @Test
    void transfer_Fails_InsufficientFunds() throws Exception {
        // 2. Establecer comportamientos simulados (Lanza excepción de negocio)
        when(transactionService.transfer(any(TransferRequestDto.class)))
                .thenThrow(new InsufficientFundsException("Saldo insuficiente en cuenta origen."));

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequestDto)))
                .andExpect(status().isBadRequest());

        // 5. Verificar interacciones
        verify(transactionService).transfer(any(TransferRequestDto.class));
    }

    //Objetivo: Fallo al obtener historial si la cuenta no existe (DataNotFoundException)
    @Test
    void getHistoryByAccountNumber_Fails_AccountNotFound() throws Exception {
        // 2. Establecer comportamientos simulados (Lanza excepción 404)
        when(transactionService.getHistoryByAccountNumber(eq(NON_EXISTENT_ACCOUNT)))
                .thenThrow(new DataNotFoundException(NON_EXISTENT_ACCOUNT, "Cuenta Bancaria"));

        // 3. y 4. Llamar al metodo a probar y verificar
        mockMvc.perform(get("/api/transactions/history/{accountNumber}", NON_EXISTENT_ACCOUNT))
                .andExpect(status().isNotFound());

        // 5. Verificar interacciones
        verify(transactionService).getHistoryByAccountNumber(eq(NON_EXISTENT_ACCOUNT));
    }

    @Test void transfer() {}
    @Test void getTransactionById() {}
    @Test void getHistoryByAccountNumber() {}
}