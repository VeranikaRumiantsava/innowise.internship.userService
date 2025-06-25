package org.innowise.internship.userservice.UserService.mappers;

import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserFullDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserShortDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { CardInfoMapper.class })
public interface UserMapper {

    User userCreateDTOToUser(UserCreateDTO userCreateDTO);

    User userUpdateDTOToUser(UserUpdateDTO userUpdateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromUserUpdateDTO(UserUpdateDTO userUpdateDTO, @MappingTarget User user);

    @Mapping(target = "fullName", expression = "java(user.getName() + \" \" + user.getSurname())")
    UserShortDTO userToUserShortDTO(User user);

    UserFullDTO userToUserFullDTO(User user);
}
