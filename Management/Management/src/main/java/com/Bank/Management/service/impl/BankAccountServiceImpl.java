package com.Bank.Management.service.impl;

import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.Transaction;
import com.Bank.Management.entity.TransactionType;
import com.Bank.Management.entity.User;
import com.Bank.Management.repository.BankAccountRepository;
import com.Bank.Management.repository.TransactionRepository;
import com.Bank.Management.repository.UserRepository;
import com.Bank.Management.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository,
                                  TransactionRepository transactionRepository,
                                  UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BankAccount createAccount(Long userId, BigDecimal initialBalance) {
        // Lógica de implementación para crear una cuenta
        return null;
    }

    @Override
    @Transactional
    public BankAccount deposit(Long accountId, BigDecimal amount) {
        // Lógica de implementación para depositar
        return null;
    }

    @Override
    @Transactional
    public BankAccount withdraw(Long accountId, BigDecimal amount) {
        // Lógica de implementación para retirar
        return null;
    }

    @Override
    public BigDecimal getBalance(Long accountId) {
        // Lógica de implementación para obtener el saldo
        return null;
    }
}
