package com.Bank.Management.dto.request;

import lombok.Data;

@Data
public class UserRegistrationDto {

    private String username;
    private String email;
    private String password;
}
