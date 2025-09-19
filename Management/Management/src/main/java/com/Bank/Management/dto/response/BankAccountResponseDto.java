package com.Bank.Management.dto.response;

import java.math.BigDecimal;

public class BankAccountResponseDto {
    private Long id;
    private String accountNumber;
    private BigDecimal balance;

    public BankAccountResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}