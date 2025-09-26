package com.Bank.Management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(length = 255)
    private String description;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id") // CORREGIDO: Se eliminó nullable = false
    private BankAccount sourceAccount;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id") // CORREGIDO: Se eliminó nullable = false
    private BankAccount targetAccount;
}