package com.Bank.Management.controller;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponseDto> createAccount(@RequestBody BankAccountRequestDto bankAccountRequestDto) {
        BankAccountResponseDto newAccount = bankAccountService.createAccount(bankAccountRequestDto);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }
}