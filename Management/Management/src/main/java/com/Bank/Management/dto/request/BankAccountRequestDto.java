package com.Bank.Management.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccountRequestDto {

    @NotNull(message = "El saldo inicial no puede ser nulo.")
    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo inicial debe ser cero o positivo.")
    private double balance;

    @NotNull(message = "El ID del usuario es obligatorio.")
    @Positive(message = "El ID del usuario debe ser un número positivo.")
    private Long userId;

}