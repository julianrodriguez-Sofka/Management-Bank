package com.Bank.Management.dto.request;

public class BankAccountRequestDto {

    private Long userId;
    private double initialBalance;

    // Constructor sin argumentos
    public BankAccountRequestDto() {
    }

    // Constructor con argumentos
    public BankAccountRequestDto(Long userId, double initialBalance) {
        this.userId = userId;
        this.initialBalance = initialBalance;
    }

    // Getters y Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }
}
