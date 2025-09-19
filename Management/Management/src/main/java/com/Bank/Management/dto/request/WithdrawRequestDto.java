package com.Bank.Management.dto.request;
import lombok.Data;
import java.math.BigDecimal;

@Data

public class WithdrawRequestDto {
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return null;
    }
}
