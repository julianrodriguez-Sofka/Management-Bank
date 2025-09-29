package com.Bank.Management.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountOperationDto {

    private String accountNumber;
    private BigDecimal amount;
}