package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.AccountOperationDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.User;
import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.InsufficientFundsException;
import com.Bank.Management.exception.InvalidOperationException;
import com.Bank.Management.mapper.BankAccountMapper;
import com.Bank.Management.repository.BankAccountRepository;
import com.Bank.Management.repository.UserRepository;
import com.Bank.Management.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BankAccountServiceImplTest {


    private BankAccountServiceImpl bankAccountService;


    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BankAccountMapper bankAccountMapper;
    @Mock
    private TransactionRepository transactionRepository;

    private User testUser;
    private BankAccountRequestDto createDto;
    private BankAccount accountEntity;
    private BankAccountResponseDto responseDto;


    private final Long USER_ID = 1L;
    private final String ACCOUNT_NUMBER = "4512345678-01";
    private final double INITIAL_BALANCE = 100.00;
    private final BigDecimal OPERATION_AMOUNT = BigDecimal.valueOf(50.00);

    @BeforeEach
    void setUp() {
        bankAccountService = new BankAccountServiceImpl(
                bankAccountRepository,
                userRepository,
                bankAccountMapper,
                transactionRepository
        );

        testUser = new User();
        testUser.setId(USER_ID);

        createDto = new BankAccountRequestDto(INITIAL_BALANCE, USER_ID);

        accountEntity = new BankAccount();
        accountEntity.setId(1L);
        accountEntity.setAccountNumber(ACCOUNT_NUMBER);
        accountEntity.setBalance(INITIAL_BALANCE);
        accountEntity.setUser(testUser);

        responseDto = new BankAccountResponseDto(1L, ACCOUNT_NUMBER, INITIAL_BALANCE, null, null);
    }

    // Objetivo: Verificar que el sistema impide la creación de una cuenta bancaria si el saldo inicial es negativo (< 0).
    @Test
    void create_fails_on_negative_balance() {
        // 1. Datos de entrada con saldo inválido
        BankAccountRequestDto negativeBalanceDto = new BankAccountRequestDto(-50.00, USER_ID);

        // 3. Llamar al metodo y esperar la excepción (400 Bad Request)
        Exception exception = assertThrows(InvalidOperationException.class, () -> {
            bankAccountService.createAccount(negativeBalanceDto);
        });

        // 4. Verificar el mensaje de error
        assertTrue(exception.getMessage().contains("El saldo inicial de la cuenta no puede ser negativo."));

        // 5. Verificar interacciones (NINGUNA interacción debe ocurrir)
        verifyNoInteractions(userRepository);
        verifyNoInteractions(bankAccountRepository);
    }

    // Objetivo: Verificar que el sistema impide un retiro si el saldo actual es menor al monto solicitado.
    @Test
    void withdraw_fails_on_insufficient_funds() {
        // 1. Datos de entrada (saldo inicial 100.00 en setUp)
        BigDecimal largeWithdrawAmount = BigDecimal.valueOf(150.00);
        AccountOperationDto operationDto = new AccountOperationDto(ACCOUNT_NUMBER, largeWithdrawAmount);

        // 2. Configurar Mocks: la cuenta existe
        when(bankAccountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(accountEntity));

        // 3. Ejecutar y Verificar (Espera la excepción de Saldo Insuficiente)
        assertThrows(InsufficientFundsException.class, () -> {
            bankAccountService.withdraw(operationDto);
        });

        // 5. Verificar interacciones (solo la búsqueda, no el guardado de la cuenta ni la transacción)
        verify(bankAccountRepository).findByAccountNumber(ACCOUNT_NUMBER);
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }
// Objetivo: Verificar que una cuenta bancaria se crea correctamente cuando se proporcionan datos válidos.
    @Test
    void createAccount_success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(bankAccountMapper.toBankAccount(any(BankAccountRequestDto.class))).thenReturn(accountEntity);
        when(bankAccountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(accountEntity);

        when(bankAccountMapper.toBankAccountResponseDto(any(BankAccount.class))).thenReturn(responseDto);

        BankAccountResponseDto result = bankAccountService.createAccount(createDto);

        assertNotNull(result);
        assertEquals(ACCOUNT_NUMBER, result.getAccountNumber());
        assertEquals(INITIAL_BALANCE, result.getBalance());

        verify(userRepository).findById(USER_ID);
        verify(bankAccountRepository, times(1)).save(any(BankAccount.class));
        verify(bankAccountMapper).toBankAccountResponseDto(any(BankAccount.class));
    }


    @Test
    void createAccount_SuccessfulPlaceholder() {

    }

    @Test
    void getAllAccounts() {

    }

    @Test
    void getAccountById() {

    }
}
