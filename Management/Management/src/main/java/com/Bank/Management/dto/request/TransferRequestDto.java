package com.Bank.Management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {

    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
}
