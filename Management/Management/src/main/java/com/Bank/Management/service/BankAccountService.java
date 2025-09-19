package com.Bank.Management.service;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;

import java.util.List;

public interface BankAccountService {

    BankAccountResponseDto createAccount(BankAccountRequestDto bankAccountRequestDto);

    List<BankAccountResponseDto> getAllAccounts();
}