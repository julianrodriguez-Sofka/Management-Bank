package com.Bank.Management.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateBankAccountDto {

    private Long id;
    private double balance;
}
