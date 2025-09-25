package com.Bank.Management.controller;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.UpdateBankAccountDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.service.BankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Cuentas Bancarias", description = "Operaciones CRUD sobre cuentas bancarias")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    @Operation(summary = "Crear una nueva cuenta bancaria para un usuario")
    public ResponseEntity<BankAccountResponseDto> createAccount(@RequestBody BankAccountRequestDto bankAccountRequestDto) {
        BankAccountResponseDto newAccount = bankAccountService.createAccount(bankAccountRequestDto);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todas las cuentas bancarias")
    public ResponseEntity<List<BankAccountResponseDto>> getAllAccounts() {
        List<BankAccountResponseDto> accounts = bankAccountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una cuenta bancaria por ID")
    public ResponseEntity<BankAccountResponseDto> getAccountById(@PathVariable Long id) {
        BankAccountResponseDto account = bankAccountService.getAccountById(id);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Actualizar el balance o tipo de una cuenta bancaria")
    public ResponseEntity<BankAccountResponseDto> updateAccount(@RequestBody UpdateBankAccountDto updateBankAccountDto) {
        BankAccountResponseDto updatedAccount = bankAccountService.updateAccount(updateBankAccountDto);
        return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cuenta bancaria por ID")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        bankAccountService.deleteAccount(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}