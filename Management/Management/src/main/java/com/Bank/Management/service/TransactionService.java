package com.Bank.Management.service;

import com.Bank.Management.entity.Transaction;

import java.util.List;

public interface TransactionService {

    List<Transaction> getTransactionsByAccountId(Long accountId);
}