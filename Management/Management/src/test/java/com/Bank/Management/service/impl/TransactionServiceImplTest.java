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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

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
    private final Long TRANSACTION_ID = 100L;


    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl(transactionRepository, bankAccountRepository, transactionMapper);

        sourceAccount = new BankAccount();
        sourceAccount.setId(1L);
        sourceAccount.setAccountNumber(SOURCE_NUM);
        sourceAccount.setBalance(INITIAL_SOURCE_BALANCE.doubleValue());
        sourceAccount.setOutgoingTransactions(new ArrayList<>());
        sourceAccount.setIncomingTransactions(new ArrayList<>());


        targetAccount = new BankAccount();
        targetAccount.setId(2L);
        targetAccount.setAccountNumber(TARGET_NUM);
        targetAccount.setBalance(INITIAL_TARGET_BALANCE.doubleValue());

        transferDto = new TransferRequestDto();
        transferDto.setSourceAccountNumber(SOURCE_NUM);
        transferDto.setDestinationAccountNumber(TARGET_NUM);
        transferDto.setAmount(TRANSFER_AMOUNT);

        testTransaction = new Transaction();
        testTransaction.setId(TRANSACTION_ID);
        testTransaction.setAmount(TRANSFER_AMOUNT.doubleValue());

        responseDto = new TransactionResponseDto();
        responseDto.setId(TRANSACTION_ID);
        responseDto.setSourceAccountNumber(SOURCE_NUM);
        responseDto.setTargetAccountNumber(TARGET_NUM);
        responseDto.setAmount(TRANSFER_AMOUNT.doubleValue());
    }

    // Objetivo: Transferencia entre cuentas (transfer) - Caso de Éxito y Casos de Error
    @Test
    void transfer_Success_UpdatesBalancesAndSavesTransaction() {
        when(bankAccountRepository.findByAccountNumber(SOURCE_NUM)).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber(TARGET_NUM)).thenReturn(Optional.of(targetAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionMapper.toTransactionResponseDto(any(Transaction.class))).thenReturn(responseDto);

        var result = transactionService.transfer(transferDto);

        assertAll("Transferencia exitosa",
                () -> assertNotNull(result),
                () -> assertEquals(150.00, sourceAccount.getBalance()),
                () -> assertEquals(100.00, targetAccount.getBalance())
        );

        verify(bankAccountRepository).findByAccountNumber(SOURCE_NUM);
        verify(bankAccountRepository).findByAccountNumber(TARGET_NUM);
        verify(bankAccountRepository, times(2)).save(any(BankAccount.class));
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // Objetivo: Transferencia entre cuentas (transfer) - Caso de Error: Fondos insuficientes
    @Test
    void transfer_Fails_ThrowsInsufficientFundsException() {
        transferDto.setAmount(BigDecimal.valueOf(300.00));

        when(bankAccountRepository.findByAccountNumber(SOURCE_NUM)).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber(TARGET_NUM)).thenReturn(Optional.of(targetAccount));

        assertThrows(InsufficientFundsException.class, () -> transactionService.transfer(transferDto));

        verify(bankAccountRepository).findByAccountNumber(SOURCE_NUM);
        verify(bankAccountRepository).findByAccountNumber(TARGET_NUM);
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }

    // Objetivo: Cuentas de Origen y Destino Iguales.
    @Test
    void transfer_Fails_ThrowsInvalidOperation_SameAccount() {
        transferDto.setDestinationAccountNumber(SOURCE_NUM);

        assertThrows(InvalidOperationException.class, () -> transactionService.transfer(transferDto));

        verify(bankAccountRepository, never()).findByAccountNumber(any());
        verifyNoInteractions(transactionRepository);
    }

    // Objetivo: Cuenta de Origen No Encontrada
    @Test
    void transfer_Fails_WhenSourceAccountNotFound() {
        when(bankAccountRepository.findByAccountNumber(SOURCE_NUM)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> transactionService.transfer(transferDto));

        verify(bankAccountRepository).findByAccountNumber(SOURCE_NUM);
        verify(bankAccountRepository, never()).findByAccountNumber(TARGET_NUM);
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }

    // Objetivo: Verificar que la transferencia falle y lance una DataNotFoundException si la cuenta de destino no se encuentra en el repositorio.
    @Test
    void transfer_Fails_WhenTargetAccountNotFound() {
        when(bankAccountRepository.findByAccountNumber(SOURCE_NUM)).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber(TARGET_NUM)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> transactionService.transfer(transferDto));

        verify(bankAccountRepository).findByAccountNumber(SOURCE_NUM);
        verify(bankAccountRepository).findByAccountNumber(TARGET_NUM);
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }

    // Objetivo: Obtener Transacción por ID (getTransactionById) - Caso de Éxito y Caso de Error
    @Test
    void getTransactionById_Success_ReturnsTransaction() {
        when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(Optional.of(testTransaction));
        when(transactionMapper.toTransactionResponseDto(any(Transaction.class))).thenReturn(responseDto);

        var result = transactionService.getTransactionById(TRANSACTION_ID);

        assertNotNull(result);
        assertEquals(TRANSACTION_ID, result.getId());

        verify(transactionRepository).findById(TRANSACTION_ID);
        verify(transactionMapper).toTransactionResponseDto(testTransaction);
    }

    // Objetivo: Obtener Transacción por ID (getTransactionById) - Caso de Error (No encontrado)
    @Test
    void getTransactionById_Fails_ThrowsDataNotFoundException() {
        when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> transactionService.getTransactionById(TRANSACTION_ID));

        verify(transactionRepository).findById(TRANSACTION_ID);
        verifyNoInteractions(transactionMapper);
    }

    // Objetivo: Obtener Historial de Transacciones por Número de Cuenta (getHistoryByAccountNumber) - Caso de Éxito y Caso de Error
    @Test
    void getHistoryByAccountNumber_Success_ReturnsListOfTransactions() {
        // Setup de la entidad para simular las listas cargadas
        Transaction incomingTransaction = new Transaction();
        Transaction outgoingTransaction = new Transaction();
        List<Transaction> outgoingList = List.of(outgoingTransaction);
        List<Transaction> incomingList = List.of(incomingTransaction);

        // El testTransaction original solo se usa para el DTO de respuesta, no para la lista
        TransactionResponseDto outgoingResponseDto = new TransactionResponseDto();
        TransactionResponseDto incomingResponseDto = new TransactionResponseDto();

        // Configurar la cuenta para que devuelva las listas
        BankAccount accountWithHistory = new BankAccount();
        accountWithHistory.setOutgoingTransactions(outgoingList);
        accountWithHistory.setIncomingTransactions(incomingList);

        // Mocking: La cuenta existe y tiene las listas cargadas
        when(bankAccountRepository.findByAccountNumber(SOURCE_NUM)).thenReturn(Optional.of(accountWithHistory));

        // Mocking: El mapper mapea cada tipo de transacción a su DTO
        when(transactionMapper.toTransactionResponseDto(outgoingTransaction)).thenReturn(outgoingResponseDto);
        when(transactionMapper.toTransactionResponseDto(incomingTransaction)).thenReturn(incomingResponseDto);

        // Ejecución
        List<TransactionResponseDto> result = transactionService.getHistoryByAccountNumber(SOURCE_NUM);

        // Aserciones: El resultado es la concatenación de ambas listas (2 transacciones)
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());

        // Verificaciones
        verify(bankAccountRepository).findByAccountNumber(SOURCE_NUM);
        // Verificamos que el mapper se llamó 1 vez por outgoing y 1 vez por incoming (Total 2 veces)
        verify(transactionMapper, times(1)).toTransactionResponseDto(outgoingTransaction);
        verify(transactionMapper, times(1)).toTransactionResponseDto(incomingTransaction);
        verifyNoInteractions(transactionRepository); // No se usó el repositorio de transacciones
    }

    // Objetivo: Obtener Historial de Transacciones por Número de Cuenta (getHistoryByAccountNumber) - Caso de Error (Cuenta no encontrada)
    @Test
    void getHistoryByAccountNumber_Fails_ThrowsDataNotFoundException() {
        when(bankAccountRepository.findByAccountNumber(SOURCE_NUM)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> transactionService.getHistoryByAccountNumber(SOURCE_NUM));

        verify(bankAccountRepository).findByAccountNumber(SOURCE_NUM);
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(transactionMapper);
    }
}