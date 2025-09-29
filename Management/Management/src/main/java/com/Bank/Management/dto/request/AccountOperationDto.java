package com.Bank.Management.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountOperationDto {

    @NotBlank(message = "El número de cuenta es obligatorio.")
    private String accountNumber;

    @NotNull(message = "El monto de la operación es obligatorio.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto de la operación debe ser mayor a cero.")
    private BigDecimal amount;
}