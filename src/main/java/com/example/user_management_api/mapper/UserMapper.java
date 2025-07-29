package com.example.user_management_api.mapper;

import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UpdateUserRequestDto;
import com.example.user_management_api.dto.UserResponseDto;
import com.example.user_management_api.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "avatarKey", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(CreateUserRequestDto dto);

    /**
     * Обновляет существующую сущность User данными из DTO.
     * @param dto  DTO с новыми данными (источник).
     * @param user Сущность для обновления (цель).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UpdateUserRequestDto dto, @MappingTarget User user);
}
