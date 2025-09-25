package com.Bank.Management.service;

import com.Bank.Management.dto.request.TransferRequestDto;
import com.Bank.Management.dto.response.TransactionResponseDto;

import java.util.List;

public interface TransactionService {

    TransactionResponseDto transfer(TransferRequestDto transferRequestDto);
    TransactionResponseDto getTransactionById(Long id);
    List<TransactionResponseDto> getHistoryByAccountNumber(String accountNumber);
}