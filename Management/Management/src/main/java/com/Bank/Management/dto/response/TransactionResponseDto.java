package com.Bank.Management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {

    private Long id;
    private double amount;
    private LocalDateTime transactionDate;
    private String description;


    private String sourceAccountNumber;
    private String targetAccountNumber;
}
