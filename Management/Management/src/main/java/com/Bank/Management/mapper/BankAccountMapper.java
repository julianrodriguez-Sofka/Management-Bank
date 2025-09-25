package com.Bank.Management.mapper;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.UpdateBankAccountDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class})
public interface BankAccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "outgoingTransactions", ignore = true)
    @Mapping(target = "incomingTransactions", ignore = true)
    @Mapping(target = "balance", source = "dto.balance")
    BankAccount toBankAccount(BankAccountRequestDto dto);

    BankAccountResponseDto toBankAccountResponseDto(BankAccount entity);

    List<BankAccountResponseDto> toBankAccountResponseDtoList(List<BankAccount> bankAccounts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "outgoingTransactions", ignore = true)
    @Mapping(target = "incomingTransactions", ignore = true)
    void updateBankAccountFromDto(UpdateBankAccountDto dto, @MappingTarget BankAccount entity);
}