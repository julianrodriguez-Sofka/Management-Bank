package com.Bank.Management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateUserDTO {

    @NotNull(message = "El ID del usuario es obligatorio para la actualización.")
    @Positive(message = "El ID del usuario debe ser un número positivo.")
    private Long id;

    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.")
    private String username;

    @Email(message = "El formato del correo electrónico es inválido.")
    private String email;

    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;
}
