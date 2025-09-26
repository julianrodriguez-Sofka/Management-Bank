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

        // 1. Validaciones iniciales
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            // Usamos IllegalArgumentException para validaciones de entrada de datos
            throw new IllegalArgumentException("El monto a transferir debe ser positivo.");
        }

        // Asumiendo que getDestinationAccountNumber() es el método correcto en tu DTO
        if (dto.getSourceAccountNumber().equals(dto.getDestinationAccountNumber())) {
            throw new IllegalArgumentException("La cuenta de origen y destino no pueden ser la misma.");
        }

        // 2. Obtener y validar existencia de cuentas
        BankAccount sourceAccount = bankAccountRepository.findByAccountNumber(dto.getSourceAccountNumber())
                .orElseThrow(() -> new RuntimeException("Cuenta de origen no encontrada: " + dto.getSourceAccountNumber()));

        // MANTENEMOS la llamada a getDestinationAccountNumber() para que compile
        BankAccount targetAccount = bankAccountRepository.findByAccountNumber(dto.getDestinationAccountNumber())
                .orElseThrow(() -> new RuntimeException("Cuenta de destino no encontrada: " + dto.getDestinationAccountNumber()));

        // 3. Validar saldo
        BigDecimal sourceBalance = BigDecimal.valueOf(sourceAccount.getBalance());

        if (sourceBalance.compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Saldo insuficiente en la cuenta de origen. Saldo actual: " + sourceAccount.getBalance());
        }

        // 4. Realizar transferencia y actualizar saldos
        sourceAccount.setBalance(sourceBalance.subtract(dto.getAmount()).doubleValue());

        BigDecimal targetBalance = BigDecimal.valueOf(targetAccount.getBalance());
        targetAccount.setBalance(targetBalance.add(dto.getAmount()).doubleValue());

        // La persistencia de saldos se mantiene transaccional
        bankAccountRepository.save(sourceAccount);
        bankAccountRepository.save(targetAccount);

        // 5. Crear Registro de Transacción
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.getAmount().doubleValue());

        // MANTENEMOS la llamada a getDestinationAccountNumber()
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

        // Combina y mapea el historial de entrada y salida
        return Stream.concat(outgoing.stream(), incoming.stream())
                .map(transactionMapper::toTransactionResponseDto)
                .toList();
    }
}