package com.Bank.Management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String dni;
    private String username;
    private String email;
    private String password;


    private List<BankAccountResponseDto> bankAccounts;
}
