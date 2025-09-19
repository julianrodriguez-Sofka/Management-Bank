package com.Bank.Management.mapper;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "balance", source = "initialBalance")
    @Mapping(target = "transactions", ignore = true)
    BankAccount toBankAccount(BankAccountRequestDto bankAccountRequestDto);

    BankAccountResponseDto toBankAccountResponseDto(BankAccount bankAccount);

    List<BankAccountResponseDto> toBankAccountResponseDtoList(List<BankAccount> bankAccountList);
}