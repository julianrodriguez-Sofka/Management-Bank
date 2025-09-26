package com.Bank.Management.service;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.UpdateBankAccountDto;
import com.Bank.Management.dto.request.AccountOperationDto; // Nueva importación para Depósito/Retiro
import com.Bank.Management.dto.response.BankAccountResponseDto;

import java.util.List;

public interface BankAccountService {


    BankAccountResponseDto createAccount(BankAccountRequestDto bankAccountRequestDto);
    List<BankAccountResponseDto> getAllAccounts();
    BankAccountResponseDto getAccountById(Long id);
    BankAccountResponseDto updateAccount(UpdateBankAccountDto updateBankAccountDto);
    void deleteAccount(Long id);
    BankAccountResponseDto deposit(AccountOperationDto operationDto);
    BankAccountResponseDto withdraw(AccountOperationDto operationDto);
}