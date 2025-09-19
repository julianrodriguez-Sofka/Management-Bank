package com.Bank.Management.mapper;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "balance", source = "initialBalance")
    BankAccount toBankAccount(BankAccountRequestDto bankAccountRequestDto);

    BankAccountResponseDto toBankAccountResponseDto(BankAccount bankAccount);
}