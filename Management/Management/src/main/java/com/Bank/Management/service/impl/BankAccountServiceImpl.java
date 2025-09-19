package com.Bank.Management.service.impl;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import com.Bank.Management.entity.User;
import com.Bank.Management.mapper.BankAccountMapper;
import com.Bank.Management.repository.BankAccountRepository;
import com.Bank.Management.repository.UserRepository;
import com.Bank.Management.service.BankAccountService;
import org.springframework.stereotype.Service;
import java.util.UUID;

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
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        BankAccount newAccount = bankAccountMapper.toBankAccount(bankAccountRequestDto);
        newAccount.setUser(user);
        newAccount.setAccountNumber(generateAccountNumber());

        BankAccount savedAccount = bankAccountRepository.save(newAccount);
        return bankAccountMapper.toBankAccountResponseDto(savedAccount);
    }

    private String generateAccountNumber() {
        return UUID.randomUUID().toString().substring(0, 12).replace("-", "");
    }
}