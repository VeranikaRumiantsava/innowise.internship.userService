package org.innowise.internship.userservice.UserService.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserFullDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserShortDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.User;

@Mapper(componentModel = "spring", uses = { CardInfoMapper.class })
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User userCreateDTOToUser(UserCreateDTO userCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromUserUpdateDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

    @Mapping(target = "fullName", expression = "java(user.getName() + \" \" + user.getSurname())")
    UserShortDTO userToUserShortDTO(User user);

    UserFullDTO userToUserFullDTO(User user);
}
