package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.UpdateBankAccountDto;
import com.Bank.Management.dto.request.AccountOperationDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.User;
import com.Bank.Management.entity.Transaction;
import com.Bank.Management.mapper.BankAccountMapper;
import com.Bank.Management.repository.BankAccountRepository;
import com.Bank.Management.repository.UserRepository;
import com.Bank.Management.repository.TransactionRepository;
import com.Bank.Management.service.BankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final BankAccountMapper bankAccountMapper;
    private final TransactionRepository transactionRepository;

    // CONSTRUCTOR ACTUALIZADO (Inyección de TransactionRepository)
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserRepository userRepository, BankAccountMapper bankAccountMapper, TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public BankAccountResponseDto createAccount(BankAccountRequestDto bankAccountRequestDto) {
        User user = userRepository.findById(bankAccountRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + bankAccountRequestDto.getUserId()));

        BankAccount bankAccount = bankAccountMapper.toBankAccount(bankAccountRequestDto);
        bankAccount.setUser(user);
        bankAccount.setAccountNumber(generateRandomAccountNumber());

        BankAccount savedAccount = bankAccountRepository.save(bankAccount);
        return bankAccountMapper.toBankAccountResponseDto(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankAccountResponseDto> getAllAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccountMapper::toBankAccountResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BankAccountResponseDto getAccountById(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con ID: " + id));
        return bankAccountMapper.toBankAccountResponseDto(account);
    }

    @Override
    @Transactional
    public BankAccountResponseDto updateAccount(UpdateBankAccountDto updateBankAccountDto) {
        BankAccount accountToUpdate = bankAccountRepository.findById(updateBankAccountDto.getId())
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con ID: " + updateBankAccountDto.getId()));

        bankAccountMapper.updateBankAccountFromDto(updateBankAccountDto, accountToUpdate);

        BankAccount updatedAccount = bankAccountRepository.save(accountToUpdate);
        return bankAccountMapper.toBankAccountResponseDto(updatedAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        if (!bankAccountRepository.existsById(id)) {
            throw new RuntimeException("Cuenta bancaria no encontrada con ID: " + id + ". La eliminación no fue posible.");
        }
        bankAccountRepository.deleteById(id);
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private BankAccount findAndValidateAccount(String accountNumber, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser un valor positivo.");
        }
        // Asumiendo que existe un findByAccountNumber en BankAccountRepository
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con número: " + accountNumber));
    }

    // -----------------------------------------------------------
    // DEPÓSITO: Se registra solo como TRANSACCIÓN ENTRANTE
    // -----------------------------------------------------------
    @Override
    @Transactional
    public BankAccountResponseDto deposit(AccountOperationDto operationDto) {
        BankAccount account = findAndValidateAccount(operationDto.getAccountNumber(), operationDto.getAmount());

        BigDecimal currentBalance = BigDecimal.valueOf(account.getBalance());

        // 1. Actualiza Saldo
        account.setBalance(currentBalance.add(operationDto.getAmount()).doubleValue());
        BankAccount updatedAccount = bankAccountRepository.save(account);

        // 2. Crea Registro de Transacción (Historial)
        Transaction transaction = new Transaction();
        transaction.setAmount(operationDto.getAmount().doubleValue());
        transaction.setDescription("Depósito en efectivo a la cuenta " + account.getAccountNumber());
        transaction.setTransactionDate(LocalDateTime.now());

        // CORRECCIÓN CLAVE: Origen es NULL (externo/cajero), solo se registra en incomingTransactions
        transaction.setSourceAccount(null);
        transaction.setTargetAccount(account);

        transactionRepository.save(transaction);

        return bankAccountMapper.toBankAccountResponseDto(updatedAccount);
    }

    // -----------------------------------------------------------
    // RETIRO: Se registra solo como TRANSACCIÓN SALIENTE
    // -----------------------------------------------------------
    @Override
    @Transactional
    public BankAccountResponseDto withdraw(AccountOperationDto operationDto) {
        BankAccount account = findAndValidateAccount(operationDto.getAccountNumber(), operationDto.getAmount());

        BigDecimal currentBalance = BigDecimal.valueOf(account.getBalance());

        // Validación de saldo insuficiente
        if (currentBalance.compareTo(operationDto.getAmount()) < 0) {
            throw new RuntimeException("Saldo insuficiente para realizar el retiro.");
        }

        // 1. Actualiza Saldo
        account.setBalance(currentBalance.subtract(operationDto.getAmount()).doubleValue());
        BankAccount updatedAccount = bankAccountRepository.save(account);

        // 2. Crea Registro de Transacción (Historial)
        Transaction transaction = new Transaction();
        transaction.setAmount(operationDto.getAmount().doubleValue());
        transaction.setDescription("Retiro de efectivo de la cuenta " + account.getAccountNumber());
        transaction.setTransactionDate(LocalDateTime.now());

        // CORRECCIÓN CLAVE: Destino es NULL (externo/cajero), solo se registra en outgoingTransactions
        transaction.setSourceAccount(account);
        transaction.setTargetAccount(null);

        transactionRepository.save(transaction);

        return bankAccountMapper.toBankAccountResponseDto(updatedAccount);
    }
}