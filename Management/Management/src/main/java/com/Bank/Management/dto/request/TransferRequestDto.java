package com.Bank.Management.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TransferRequestDto {

    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
}
