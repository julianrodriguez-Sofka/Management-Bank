package com.Bank.Management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegistrationDto {

    private String username;
    private String email;
    private String password;
}
