package com.Bank.Management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class UserRegistrationDto {

    private String username;
    private String email;
    private String password;
}
