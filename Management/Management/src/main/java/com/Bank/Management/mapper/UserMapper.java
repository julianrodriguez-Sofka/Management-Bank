package com.Bank.Management.mapper;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    User toUser(UserRegistrationDto userRegistrationDto);

    UserResponseDto toUserResponseDto(User user);

    List<UserResponseDto> toUserResponseDtoList(List<User> userList);
}