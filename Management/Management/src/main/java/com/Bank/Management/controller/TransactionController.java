package com.Bank.Management.controller;

import com.Bank.Management.dto.request.TransferRequestDto;
import com.Bank.Management.dto.response.TransactionResponseDto;
import com.Bank.Management.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transacciones", description = "Operaciones de transferencias y consulta de historial")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping("/transfer")
    @Operation(summary = "Realizar una transferencia entre cuentas")
    public ResponseEntity<TransactionResponseDto> transfer(@RequestBody TransferRequestDto dto) {
        TransactionResponseDto response = transactionService.transfer(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtener los detalles de una transacción por ID")
    public ResponseEntity<TransactionResponseDto> getTransactionById(@PathVariable Long id) {
        TransactionResponseDto response = transactionService.getTransactionById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/history/{accountNumber}")
    @Operation(summary = "Obtener todo el historial de transacciones (entrantes y salientes) de una cuenta")
    public ResponseEntity<List<TransactionResponseDto>> getHistoryByAccountNumber(@PathVariable String accountNumber) {
        List<TransactionResponseDto> response = transactionService.getHistoryByAccountNumber(accountNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}