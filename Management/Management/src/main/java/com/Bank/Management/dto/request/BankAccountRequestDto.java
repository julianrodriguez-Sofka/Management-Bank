package com.Bank.Management.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data

public class BankAccountRequestDto {
    private Long userId;
    private BigDecimal initialBalance;

    public long getUserId() {
        return 0;
    }

    public BigDecimal getInitialBalance() {
        return null;
    }
}
