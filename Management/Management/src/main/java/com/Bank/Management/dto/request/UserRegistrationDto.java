package com.Bank.Management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationDto {

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    private String username;

    @Email(message = "El formato del correo electrónico es inválido.")
    @NotBlank(message = "El correo electrónico es obligatorio.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;
}
