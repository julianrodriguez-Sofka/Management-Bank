package com.Bank.Management.mapper;

import com.Bank.Management.dto.request.BankAccountRequestDto;
import com.Bank.Management.dto.request.UpdateBankAccountDto;
import com.Bank.Management.dto.response.BankAccountResponseDto;
import com.Bank.Management.entity.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    BankAccount toBankAccount(BankAccountRequestDto dto);

    BankAccountResponseDto toBankAccountResponseDto(BankAccount entity);

    List<BankAccountResponseDto> toBankAccountResponseDtoList(List<BankAccount> bankAccounts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateBankAccountFromDto(UpdateBankAccountDto dto, @MappingTarget BankAccount entity);
}