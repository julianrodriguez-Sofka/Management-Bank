package com.Bank.Management.mapper;

import com.Bank.Management.dto.request.UserRegistrationDto;
import com.Bank.Management.dto.request.UpdateUserDTO;
import com.Bank.Management.dto.response.UserResponseDto;
import com.Bank.Management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Convierte el DTO de registro a una entidad User.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    User toUser(UserRegistrationDto userRegistrationDto);

    // Convierte una entidad User a un DTO de respuesta.
    UserResponseDto toUserResponseDto(User user);

    // Convierte una lista de entidades User a una lista de DTOs de respuesta.
    List<UserResponseDto> toUserResponseDtoList(List<User> userList);

    // Mapea los cambios de UpdateUserDTO a la entidad User existente.
    // Ignora el ID para no cambiar la identidad del objeto.
    @Mapping(target = "id", ignore = true)
    void updateUserFromDto(UpdateUserDTO dto, @MappingTarget User user);
}