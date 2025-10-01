package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.AccountOperationDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.User;
import com.Bank.Management.entity.Transaction;
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
import java.util.List;
import java.util.ArrayList;

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
    private final Long ACCOUNT_ID = 1L;
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
        accountEntity.setId(ACCOUNT_ID);
        accountEntity.setAccountNumber(ACCOUNT_NUMBER);
        accountEntity.setBalance(INITIAL_BALANCE);
        accountEntity.setUser(testUser);

        responseDto = new BankAccountResponseDto(ACCOUNT_ID, ACCOUNT_NUMBER, INITIAL_BALANCE, null, null);
    }

    // Objetivo: Validar que la creación de cuenta falle si el saldo inicial es negativo.
    @Test
    void create_fails_on_negative_balance() {
        BankAccountRequestDto negativeBalanceDto = new BankAccountRequestDto(-50.00, USER_ID);

        Exception exception = assertThrows(InvalidOperationException.class, () -> {
            bankAccountService.createAccount(negativeBalanceDto);
        });

        assertTrue(exception.getMessage().contains("El saldo inicial de la cuenta no puede ser negativo."));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(bankAccountRepository);
    }

    // Objetivo: Validar que el retiro falle si los fondos son insuficientes.
    @Test
    void withdraw_fails_on_insufficient_funds() {
        BigDecimal largeWithdrawAmount = BigDecimal.valueOf(150.00);
        AccountOperationDto operationDto = new AccountOperationDto(ACCOUNT_NUMBER, largeWithdrawAmount);

        when(bankAccountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(accountEntity));

        assertThrows(InsufficientFundsException.class, () -> {
            bankAccountService.withdraw(operationDto);
        });

        verify(bankAccountRepository).findByAccountNumber(ACCOUNT_NUMBER);
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }

    // Objetivo: Validar la creación exitosa de una cuenta bancaria.
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

    // Objetivo: Fallo en la creación de cuenta si el usuario no existe.
    @Test
    void createAccount_fails_on_user_not_found() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            bankAccountService.createAccount(createDto);
        });

        verify(userRepository).findById(USER_ID);
        verifyNoInteractions(bankAccountRepository);
        verifyNoInteractions(bankAccountMapper);
    }

    // Objetivo: Retiro exitoso
    @Test
    void withdraw_success() {
        AccountOperationDto operationDto = new AccountOperationDto(ACCOUNT_NUMBER, OPERATION_AMOUNT);

        when(bankAccountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(accountEntity));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(accountEntity);
        when(bankAccountMapper.toBankAccountResponseDto(any(BankAccount.class))).thenReturn(responseDto);

        BankAccountResponseDto result = bankAccountService.withdraw(operationDto);
        double expectedBalance = INITIAL_BALANCE - OPERATION_AMOUNT.doubleValue();

        assertNotNull(result);
        assertEquals(expectedBalance, accountEntity.getBalance());

        verify(bankAccountRepository).findByAccountNumber(ACCOUNT_NUMBER);
        verify(bankAccountRepository).save(accountEntity);
        verify(transactionRepository).save(any(Transaction.class));
    }

    // Objetivo: Retiro falla si la cuenta no existe.
    @Test
    void withdraw_fails_on_account_not_found() {
        AccountOperationDto operationDto = new AccountOperationDto(ACCOUNT_NUMBER, OPERATION_AMOUNT);

        when(bankAccountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            bankAccountService.withdraw(operationDto);
        });

        verify(bankAccountRepository).findByAccountNumber(ACCOUNT_NUMBER);
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }

    // Objetivo: Depósito exitoso
    @Test
    void deposit_success() {
        AccountOperationDto operationDto = new AccountOperationDto(ACCOUNT_NUMBER, OPERATION_AMOUNT);

        when(bankAccountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.of(accountEntity));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(accountEntity);
        when(bankAccountMapper.toBankAccountResponseDto(any(BankAccount.class))).thenReturn(responseDto);

        BankAccountResponseDto result = bankAccountService.deposit(operationDto);
        double expectedBalance = INITIAL_BALANCE + OPERATION_AMOUNT.doubleValue();

        assertNotNull(result);
        assertEquals(expectedBalance, accountEntity.getBalance());

        verify(bankAccountRepository).findByAccountNumber(ACCOUNT_NUMBER);
        verify(bankAccountRepository).save(accountEntity);
        verify(transactionRepository).save(any(Transaction.class));
    }

    // Objetivo: Depósito falla si la cuenta no existe.
    @Test
    void deposit_fails_on_account_not_found() {
        AccountOperationDto operationDto = new AccountOperationDto(ACCOUNT_NUMBER, OPERATION_AMOUNT);

        when(bankAccountRepository.findByAccountNumber(ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            bankAccountService.deposit(operationDto);
        });

        verify(bankAccountRepository).findByAccountNumber(ACCOUNT_NUMBER);
        verify(bankAccountRepository, never()).save(any());
        verifyNoInteractions(transactionRepository);
    }

    // Objetivo: Obtener todas las cuentas (caso de éxito)
    @Test
    void getAllAccounts_success() {
        List<BankAccount> accountList = List.of(accountEntity);

        when(bankAccountRepository.findAll()).thenReturn(accountList);
        when(bankAccountMapper.toBankAccountResponseDto(any(BankAccount.class))).thenReturn(responseDto);

        List<BankAccountResponseDto> result = bankAccountService.getAllAccounts();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(ACCOUNT_NUMBER, result.get(0).getAccountNumber());

        verify(bankAccountRepository).findAll();
        verify(bankAccountMapper, times(accountList.size())).toBankAccountResponseDto(any(BankAccount.class));
    }

    // Objetivo: Obtener cuenta por ID (caso de éxito)
    @Test
    void getAccountById_success() {
        when(bankAccountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(accountEntity));
        when(bankAccountMapper.toBankAccountResponseDto(accountEntity)).thenReturn(responseDto);

        BankAccountResponseDto result = bankAccountService.getAccountById(ACCOUNT_ID);

        assertNotNull(result);
        assertEquals(ACCOUNT_NUMBER, result.getAccountNumber());

        verify(bankAccountRepository).findById(ACCOUNT_ID);
        verify(bankAccountMapper).toBankAccountResponseDto(accountEntity);
    }

    // Objetivo: Obtener cuenta por ID (caso de fallo - no encontrado)
    @Test
    void getAccountById_fails_on_not_found() {
        when(bankAccountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> {
            bankAccountService.getAccountById(ACCOUNT_ID);
        });

        verify(bankAccountRepository).findById(ACCOUNT_ID);
        verifyNoInteractions(bankAccountMapper);
    }
}
