package com.Bank.Management.service;

import java.math.BigDecimal;
import com.Bank.Management.entity.BankAccount;

public interface BankAccountService {

    BankAccount createAccount(Long userId, BigDecimal initialBalance);
    BankAccount deposit(Long accountId, BigDecimal amount);
    BankAccount withdraw(Long accountId, BigDecimal amount);
    BigDecimal getBalance(Long accountId);
}