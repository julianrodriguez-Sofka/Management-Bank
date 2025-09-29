package com.Bank.Management.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TransferRequestDto {

    @NotBlank(message = "El número de cuenta de origen es obligatorio.")
    private String sourceAccountNumber;

    @NotBlank(message = "El número de cuenta de destino es obligatorio.")
    private String destinationAccountNumber;

    @NotNull(message = "El monto de la transferencia es obligatorio.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto a transferir debe ser mayor a cero.")
    private BigDecimal amount;
}
