package com.Bank.Management.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateUserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
}
