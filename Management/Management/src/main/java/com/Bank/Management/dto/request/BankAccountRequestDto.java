package com.Bank.Management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data; // Incluye @Getter y @Setter
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequestDto {

    private double balance;
    private Long userId;
}