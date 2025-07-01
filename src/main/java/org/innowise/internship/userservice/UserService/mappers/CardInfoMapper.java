package org.innowise.internship.userservice.UserService.mappers;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCreateDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoFullDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoUpdateDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoWithoutUserDTO;
import org.innowise.internship.userservice.UserService.entities.CardInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface CardInfoMapper {
    CardInfoFullDTO cardInfoToCardInfoFullDTO(CardInfo cardInfo);

    CardInfoWithoutUserDTO cardInfoToCardInfoWithoutUserDTO(CardInfo cardInfo);

    CardInfo cardInfoCreateDTOToCardInfo(CardInfoCreateDTO cardInfoCreateDTO);

    CardInfo cardInfoUpdateDTOToCardInfo(CardInfoUpdateDTO cardInfoUpdateDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCardInfoFromCardInfoUpdateDTO(CardInfoUpdateDTO cardInfoUpdateDT, @MappingTarget CardInfo cardInfo);


}
