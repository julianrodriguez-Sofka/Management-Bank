package com.Bank.Management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// Importa el DTO que crearemos a continuación
import com.Bank.Management.dto.response.TransactionResponseDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponseDto {

    private Long id;
    private String accountNumber;
    private double balance;


    private List<TransactionResponseDto> outgoingTransactions;
    private List<TransactionResponseDto> incomingTransactions;
}