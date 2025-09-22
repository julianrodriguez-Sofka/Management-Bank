package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.UpdateBankAccountDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.User;
import com.Bank.Management.mapper.BankAccountMapper;
import com.Bank.Management.repository.BankAccountRepository;
import com.Bank.Management.repository.UserRepository;
import com.Bank.Management.service.BankAccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final BankAccountMapper bankAccountMapper;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository, UserRepository userRepository, BankAccountMapper bankAccountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
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
    public List<BankAccountResponseDto> getAllAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccountMapper::toBankAccountResponseDto)
                .toList();
    }

    @Override
    public BankAccountResponseDto getAccountById(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con ID: " + id));
        return bankAccountMapper.toBankAccountResponseDto(account);
    }

    @Override
    public BankAccountResponseDto updateAccount(UpdateBankAccountDto updateBankAccountDto) {
        BankAccount accountToUpdate = bankAccountRepository.findById(updateBankAccountDto.getId())
                .orElseThrow(() -> new RuntimeException("Cuenta bancaria no encontrada con ID: " + updateBankAccountDto.getId()));

        bankAccountMapper.updateBankAccountFromDto(updateBankAccountDto, accountToUpdate);
        BankAccount updatedAccount = bankAccountRepository.save(accountToUpdate);
        return bankAccountMapper.toBankAccountResponseDto(updatedAccount);
    }

    @Override
    public void deleteAccount(Long id) {
        if (!bankAccountRepository.existsById(id)) {
            throw new RuntimeException("Cuenta bancaria no encontrada con ID: " + id + ". La eliminación no fue posible.");
        }
        bankAccountRepository.deleteById(id);
    }

    // Método privado para generar un número de cuenta numérico
    private String generateRandomAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        // Genera un número de 12 dígitos
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10)); // Añade un dígito aleatorio del 0 al 9
        }
        return sb.toString();
    }
}