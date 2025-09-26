package com.Bank.Management.repository;

import com.Bank.Management.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {


    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
