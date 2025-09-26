package com.Bank.Management.service.impl;


import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.InsufficientFundsException;
import com.Bank.Management.exception.InvalidOperationException;
import com.Bank.Management.dto.request.TransferRequestDto;
import com.Bank.Management.dto.response.TransactionResponseDto;
import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.Transaction;
import com.Bank.Management.repository.BankAccountRepository;
import com.Bank.Management.repository.TransactionRepository;
import com.Bank.Management.mapper.TransactionMapper;
import com.Bank.Management.service.TransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Stream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("El monto a transferir debe ser positivo.");
        }

        if (dto.getSourceAccountNumber().equals(dto.getDestinationAccountNumber())) {
            throw new InvalidOperationException("La cuenta de origen y destino no pueden ser la misma.");
        }

        BankAccount sourceAccount = bankAccountRepository.findByAccountNumber(dto.getSourceAccountNumber())
                .orElseThrow(() -> new DataNotFoundException(dto.getSourceAccountNumber(), "Cuenta de origen"));

        BankAccount targetAccount = bankAccountRepository.findByAccountNumber(dto.getDestinationAccountNumber())
                .orElseThrow(() -> new DataNotFoundException(dto.getDestinationAccountNumber(), "Cuenta de destino"));

        BigDecimal sourceBalance = BigDecimal.valueOf(sourceAccount.getBalance());

        if (sourceBalance.compareTo(dto.getAmount()) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente en la cuenta de origen.");
        }

        sourceAccount.setBalance(sourceBalance.subtract(dto.getAmount()).doubleValue());


        BigDecimal targetBalance = BigDecimal.valueOf(targetAccount.getBalance());
        targetAccount.setBalance(targetBalance.add(dto.getAmount()).doubleValue());

        bankAccountRepository.save(sourceAccount);
        bankAccountRepository.save(targetAccount);

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
                .orElseThrow(() -> new DataNotFoundException(id, "Transacción"));

        return transactionMapper.toTransactionResponseDto(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getHistoryByAccountNumber(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new DataNotFoundException(accountNumber, "Cuenta Bancaria"));


        List<Transaction> outgoing = account.getOutgoingTransactions();
        List<Transaction> incoming = account.getIncomingTransactions();

        return Stream.concat(outgoing.stream(), incoming.stream())
                .map(transactionMapper::toTransactionResponseDto)
                .toList();
    }
}