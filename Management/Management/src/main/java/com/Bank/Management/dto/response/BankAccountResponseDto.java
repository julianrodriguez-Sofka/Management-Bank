package com.Bank.Management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponseDto {

    private Long id;
    private String accountNumber;
    private double balance;
}