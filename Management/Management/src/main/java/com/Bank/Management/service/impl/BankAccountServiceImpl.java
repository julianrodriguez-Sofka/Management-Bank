package com.Bank.Management.service.impl;


import com.Bank.Management.exception.DataNotFoundException;
import com.Bank.Management.exception.DuplicatedDataException;
import com.Bank.Management.exception.InsufficientFundsException;
import com.Bank.Management.exception.InvalidOperationException;
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

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserRepository userRepository, BankAccountMapper bankAccountMapper, TransactionRepository transactionRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public BankAccountResponseDto createAccount(BankAccountRequestDto bankAccountRequestDto) {

        if (bankAccountRequestDto.getBalance() < 0) {
            throw new InvalidOperationException("El saldo inicial de la cuenta no puede ser negativo.");
        }

        User user = userRepository.findById(bankAccountRequestDto.getUserId())
                .orElseThrow(() -> new DataNotFoundException(bankAccountRequestDto.getUserId(), "Usuario"));

        String newAccountNumber = generateUniqueAccountNumber();

        BankAccount bankAccount = bankAccountMapper.toBankAccount(bankAccountRequestDto);
        bankAccount.setUser(user);
        bankAccount.setAccountNumber(newAccountNumber);

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
                .orElseThrow(() -> new DataNotFoundException(id, "Cuenta bancaria"));
        return bankAccountMapper.toBankAccountResponseDto(account);
    }

    @Override
    @Transactional
    public BankAccountResponseDto updateAccount(UpdateBankAccountDto updateBankAccountDto) {
        BankAccount accountToUpdate = bankAccountRepository.findById(updateBankAccountDto.getId())
                .orElseThrow(() -> new DataNotFoundException(updateBankAccountDto.getId(), "Cuenta bancaria"));

        bankAccountMapper.updateBankAccountFromDto(updateBankAccountDto, accountToUpdate);

        BankAccount updatedAccount = bankAccountRepository.save(accountToUpdate);
        return bankAccountMapper.toBankAccountResponseDto(updatedAccount);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        if (!bankAccountRepository.existsById(id)) {
            throw new DataNotFoundException(id, "Cuenta bancaria");
        }
        bankAccountRepository.deleteById(id);
    }

    // Método privado para generar un número de cuenta único y con formato realista
    private String generateUniqueAccountNumber() {
        String newAccountNumber;
        do {
            newAccountNumber = generateRandomAccountNumber();
        } while (bankAccountRepository.findByAccountNumber(newAccountNumber).isPresent());
        return newAccountNumber;
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        sb.append("45");

        for (int i = 0; i < 8; i++) {
            sb.append(random.nextInt(10));
        }

        sb.append("-");
        sb.append(random.nextInt(10));
        sb.append(random.nextInt(10));

        return sb.toString();
    }

    private BankAccount findAndValidateAccount(String accountNumber, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidOperationException("El monto debe ser un valor positivo.");
        }

        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new DataNotFoundException(accountNumber, "Cuenta bancaria"));
    }

    @Override
    @Transactional
    public BankAccountResponseDto deposit(AccountOperationDto operationDto) {
        BankAccount account = findAndValidateAccount(operationDto.getAccountNumber(), operationDto.getAmount());

        BigDecimal currentBalance = BigDecimal.valueOf(account.getBalance());

        account.setBalance(currentBalance.add(operationDto.getAmount()).doubleValue());
        BankAccount updatedAccount = bankAccountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(operationDto.getAmount().doubleValue());
        transaction.setDescription("Depósito en efectivo a la cuenta " + account.getAccountNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSourceAccount(null);
        transaction.setTargetAccount(account);

        transactionRepository.save(transaction);

        return bankAccountMapper.toBankAccountResponseDto(updatedAccount);
    }

    @Override
    @Transactional
    public BankAccountResponseDto withdraw(AccountOperationDto operationDto) {
        BankAccount account = findAndValidateAccount(operationDto.getAccountNumber(), operationDto.getAmount());

        BigDecimal currentBalance = BigDecimal.valueOf(account.getBalance());

        if (currentBalance.compareTo(operationDto.getAmount()) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente para realizar el retiro.");
        }

        account.setBalance(currentBalance.subtract(operationDto.getAmount()).doubleValue());
        BankAccount updatedAccount = bankAccountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(operationDto.getAmount().doubleValue());
        transaction.setDescription("Retiro de efectivo de la cuenta " + account.getAccountNumber());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setSourceAccount(account);
        transaction.setTargetAccount(null);

        transactionRepository.save(transaction);

        return bankAccountMapper.toBankAccountResponseDto(updatedAccount);
    }
}