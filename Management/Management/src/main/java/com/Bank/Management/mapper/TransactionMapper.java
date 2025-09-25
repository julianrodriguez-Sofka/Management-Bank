package com.Bank.Management.mapper;

import com.Bank.Management.dto.response.TransactionResponseDto;
import com.Bank.Management.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "sourceAccountNumber", source = "sourceAccount.accountNumber")
    @Mapping(target = "targetAccountNumber", source = "targetAccount.accountNumber")
    TransactionResponseDto toTransactionResponseDto(Transaction transaction);

    List<TransactionResponseDto> toTransactionResponseDtoList(List<Transaction> transactions);
}
