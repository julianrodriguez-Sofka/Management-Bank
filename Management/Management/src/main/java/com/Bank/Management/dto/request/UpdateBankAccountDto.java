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
public class UpdateBankAccountDto {

    @NotNull(message = "El ID de la cuenta es obligatorio para la actualización.")
    @Positive(message = "El ID de la cuenta debe ser un número positivo.")
    private Long id;

    @NotNull(message = "El saldo no puede ser nulo.")
    @DecimalMin(value = "0.00", inclusive = true, message = "El saldo de la cuenta debe ser cero o positivo.")
    private double balance;
}
