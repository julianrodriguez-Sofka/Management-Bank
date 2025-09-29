package com.Bank.Management.dto.response;

import lombok.*;

import java.util.List;

// Importa el DTO que crearemos a continuación
import com.Bank.Management.dto.response.TransactionResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccountResponseDto {

    private Long id;
    private String accountNumber;
    private double balance;


    private List<TransactionResponseDto> outgoingTransactions;
    private List<TransactionResponseDto> incomingTransactions;
}