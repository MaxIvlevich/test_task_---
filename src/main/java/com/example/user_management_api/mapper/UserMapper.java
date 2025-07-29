package com.example.user_management_api.mapper;

import com.example.user_management_api.dto.CreateUserRequestDto;
import com.example.user_management_api.dto.UpdateUserDataRequestDto;
import com.example.user_management_api.dto.UserContactInfoResponseDto;
import com.example.user_management_api.dto.UserDataResponseDto;
import com.example.user_management_api.dto.UserResponseDto;
import com.example.user_management_api.model.User;
import com.example.user_management_api.model.UserData;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * "Собирает" полный UserResponseDto из двух связанных сущностей: User и UserData.
     * MapStruct автоматически поймет, что нужно взять поля из user.userData.
     *
     * @param user Главная сущность User.
     * @return Полный DTO для ответа клиенту.
     */
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phoneNumber", source = "user.phoneNumber")
    @Mapping(target = "roles", source = "user.roles")
    @Mapping(target = "firstName", source = "user.userData.firstName")
    @Mapping(target = "lastName", source = "user.userData.lastName")
    @Mapping(target = "patronymic", source = "user.userData.patronymic")
    @Mapping(target = "dateOfBirth", source = "user.userData.dateOfBirth")
    @Mapping(target = "avatarKey", source = "user.userData.avatarKey")
    UserResponseDto toUserResponseDto(User user);


    /**
     * Создает сущность User из DTO. Персональные данные игнорируются.
     *
     * @param dto DTO для создания.
     * @return Сущность User.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "userData", ignore = true)
    User toUserEntity(CreateUserRequestDto dto);

    /**
     * Создает сущность UserData из DTO. Данные для аутентификации игнорируются.
     *
     * @param dto DTO для создания.
     * @return Сущность UserData.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "avatarKey", ignore = true)
    UserData toUserDataEntity(CreateUserRequestDto dto);


    /**
     * Обновляет существующую сущность UserData данными из DTO.
     * Игнорирует null-поля в DTO.
     *
     * @param dto      DTO с обновленными данными.
     * @param userData Сущность UserData для обновления (цель).
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "avatarKey", ignore = true)
    void updateUserDataFromDto(UpdateUserDataRequestDto dto, @MappingTarget UserData userData);

    /**
     * Преобразует сущность пользователя в DTO, содержащий контактную информацию.
     *
     * @param user Сущность пользователя.
     * @return DTO с контактной информацией (email, телефон и т.д.).
     */
    UserContactInfoResponseDto toUserContactInfoDto(User user);

    /**
     * Преобразует сущность UserData в DTO с детальной информацией.
     *
     * @param userData Сущность, содержащая детальную информацию о пользователе.
     * @return DTO с подробными пользовательскими данными (например, адрес, фото и др.).
     */
    @Mapping(target = "userId", source = "user.id")
    UserDataResponseDto toUserDataDto(UserData userData);
}
