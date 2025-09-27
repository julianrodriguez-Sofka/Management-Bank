package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.TransferRequestDto;
import com.Bank.Management.dto.response.TransactionResponseDto;
import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.Transaction;
import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.InsufficientFundsException;
import com.Bank.Management.exception.InvalidOperationException;
import com.Bank.Management.mapper.TransactionMapper;
import com.Bank.Management.repository.BankAccountRepository;
import com.Bank.Management.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    private BankAccount sourceAccount;
    private BankAccount targetAccount;
    private TransferRequestDto transferDto;
    private Transaction testTransaction;
    private TransactionResponseDto responseDto;

    private final String SOURCE_NUM = "111-ORIGEN";
    private final String TARGET_NUM = "222-DESTINO";
    private final BigDecimal INITIAL_SOURCE_BALANCE = BigDecimal.valueOf(200.00);
    private final BigDecimal INITIAL_TARGET_BALANCE = BigDecimal.valueOf(50.00);
    private final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(50.00);


    @BeforeEach
    void setUp() {
        // Inicialización de Cuenta Origen
        sourceAccount = new BankAccount();
        sourceAccount.setId(1L);
        sourceAccount.setAccountNumber(SOURCE_NUM);
        sourceAccount.setBalance(INITIAL_SOURCE_BALANCE.doubleValue());

        // Inicialización de Cuenta Destino
        targetAccount = new BankAccount();
        targetAccount.setId(2L);
        targetAccount.setAccountNumber(TARGET_NUM);
        targetAccount.setBalance(INITIAL_TARGET_BALANCE.doubleValue());

        // DTO de Transferencia Válida
        transferDto = new TransferRequestDto();
        transferDto.setSourceAccountNumber(SOURCE_NUM);
        transferDto.setDestinationAccountNumber(TARGET_NUM);
        transferDto.setAmount(TRANSFER_AMOUNT);

        // Entidad de Transacción simulada
        testTransaction = new Transaction();
        testTransaction.setId(100L);
        testTransaction.setAmount(TRANSFER_AMOUNT.doubleValue());

        // DTO de Respuesta simulado (CORREGIDO: Solo usamos campos existentes)
        responseDto = new TransactionResponseDto();
        responseDto.setId(100L);
        responseDto.setSourceAccountNumber(SOURCE_NUM);
        responseDto.setTargetAccountNumber(TARGET_NUM);
        responseDto.setAmount(TRANSFER_AMOUNT.doubleValue());

    }

  // Objetivo: Transferencia - Caso de Éxito

    @Test
    void transfer_Success_UpdatesBalancesAndSavesTransaction() {
        // 2. Establecer comportamientos simulados
        when(bankAccountRepository.findByAccountNumber(SOURCE_NUM)).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber(TARGET_NUM)).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionMapper.toTransactionResponseDto(any(Transaction.class))).thenReturn(responseDto);

        // 3. Llamar al método a probar
        var result = transactionService.transfer(transferDto);

        // 4. Verificar los resultados
        assertAll("Transferencia exitosa",
                () -> assertNotNull(result),
                () -> assertEquals(150.00, sourceAccount.getBalance()),
                () -> assertEquals(100.00, targetAccount.getBalance())
        );

        // 5. Verificar interacciones
        verify(bankAccountRepository).findByAccountNumber(SOURCE_NUM);
        verify(bankAccountRepository).findByAccountNumber(TARGET_NUM);
        verify(bankAccountRepository, times(2)).save(any(BankAccount.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }


    // Objetivo: Transferencia - Caso de Fallo (Saldo Insuficiente)

    @Test
    void transfer_Fails_ThrowsInsufficientFundsException() {
        // 1. Datos de entrada (Monto mayor al saldo)
        transferDto.setAmount(BigDecimal.valueOf(300.00));

        // 2. Establecer comportamientos simulados
        when(bankAccountRepository.findByAccountNumber(SOURCE_NUM)).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber(TARGET_NUM)).thenReturn(Optional.of(targetAccount));

        // 3. Llamar al método y esperar la excepción
        assertThrows(InsufficientFundsException.class, () -> transactionService.transfer(transferDto));

        // 5. Verificar interacciones
        verify(bankAccountRepository).findByAccountNumber(SOURCE_NUM);
        verify(bankAccountRepository).findByAccountNumber(TARGET_NUM);
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void getTransactionById() {
    }

    @Test
    void getHistoryByAccountNumber() {
    }
}