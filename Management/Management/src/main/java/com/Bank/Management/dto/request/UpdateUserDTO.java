package com.Bank.Management.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
}
