package com.Bank.Management.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BankAccountRequestDto {

    private double balance;
    private Long userId;

}