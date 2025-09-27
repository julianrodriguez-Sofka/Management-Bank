package com.Bank.Management.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter

public class TransactionResponseDto {

    private Long id;
    private double amount;
    private LocalDateTime transactionDate;
    private String description;


    private String sourceAccountNumber;
    private String targetAccountNumber;
}
