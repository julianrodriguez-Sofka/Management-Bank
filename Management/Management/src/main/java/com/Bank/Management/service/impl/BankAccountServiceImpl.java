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

import java.util.List;
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
        // Busca al usuario por su ID.
        User user = userRepository.findById(bankAccountRequestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // Mapea el DTO a la entidad.
        BankAccount bankAccount = bankAccountMapper.toBankAccount(bankAccountRequestDto);

        // Asigna el usuario a la cuenta y genera un número de cuenta único.
        bankAccount.setUser(user);
        bankAccount.setAccountNumber(UUID.randomUUID().toString());

        // Guarda la nueva cuenta en la base de datos.
        BankAccount savedAccount = bankAccountRepository.save(bankAccount);

        // Devuelve el DTO de respuesta.
        return bankAccountMapper.toBankAccountResponseDto(savedAccount);
    }

    @Override
    public List<BankAccountResponseDto> getAllAccounts() {
        List<BankAccount> accounts = bankAccountRepository.findAll();
        return bankAccountMapper.toBankAccountResponseDtoList(accounts);
    }
}