package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.TransferRequestDto;
import com.Bank.Management.dto.response.TransactionResponseDto;
import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.Transaction;
import com.Bank.Management.mapper.TransactionMapper;
import com.Bank.Management.repository.BankAccountRepository;
import com.Bank.Management.repository.TransactionRepository;
import com.Bank.Management.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @Transactional
    public TransactionResponseDto transfer(TransferRequestDto dto) {

        BankAccount sourceAccount = bankAccountRepository.findByAccountNumber(dto.getSourceAccountNumber())
                .orElseThrow(() -> new RuntimeException("Cuenta de origen no encontrada: " + dto.getSourceAccountNumber()));

        BankAccount targetAccount = bankAccountRepository.findByAccountNumber(dto.getDestinationAccountNumber())
                .orElseThrow(() -> new RuntimeException("Cuenta de destino no encontrada: " + dto.getDestinationAccountNumber()));

        if (sourceAccount.getId().equals(targetAccount.getId())) {
            throw new RuntimeException("La cuenta de origen y destino no pueden ser la misma.");
        }

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto a transferir debe ser positivo.");
        }

        BigDecimal sourceBalance = BigDecimal.valueOf(sourceAccount.getBalance());

        if (sourceBalance.compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Saldo insuficiente en la cuenta de origen.");
        }

        sourceAccount.setBalance(sourceBalance.subtract(dto.getAmount()).doubleValue());

        BigDecimal targetBalance = BigDecimal.valueOf(targetAccount.getBalance());
        targetAccount.setBalance(targetBalance.add(dto.getAmount()).doubleValue());

        bankAccountRepository.save(sourceAccount);
        bankAccountRepository.save(targetAccount);

        // Crear Registro de Transacción
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount().doubleValue());
        transaction.setDescription("Transferencia de " + dto.getSourceAccountNumber() + " a " + dto.getDestinationAccountNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toTransactionResponseDto(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDto getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada con ID: " + id));

        return transactionMapper.toTransactionResponseDto(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getHistoryByAccountNumber(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con número: " + accountNumber));

        List<Transaction> outgoing = account.getOutgoingTransactions();
        List<Transaction> incoming = account.getIncomingTransactions();

        return Stream.concat(outgoing.stream(), incoming.stream())
                .map(transactionMapper::toTransactionResponseDto)
                .toList();
    }
}