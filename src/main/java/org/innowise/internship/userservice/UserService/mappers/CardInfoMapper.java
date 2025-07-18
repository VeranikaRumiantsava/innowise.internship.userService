package org.innowise.internship.userservice.UserService.mappers;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCacheDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoResponseDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCreateDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.CardInfo;



@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface CardInfoMapper {
    @Mapping(target = "userId", expression = "java(cardInfo.getUser().getId())")
    CardInfoResponseDTO cardInfoToCardInfoResponseDTO(CardInfo cardInfo);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    CardInfo cardInfoCreateDTOToCardInfo(CardInfoCreateDTO cardInfoCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCardInfoFromCardInfoUpdateDTO(CardInfoUpdateDTO cardInfoUpdateDTO, @MappingTarget CardInfo cardInfo);

    CardInfoResponseDTO cardInfoCacheDTOToCardInfoResponseDTO(CardInfoCacheDTO cardInfoCacheDTO);

    CardInfoCacheDTO cardInfoResponseDTOToCardInfoCacheDTO(CardInfoResponseDTO cardInfoResponseDTO);

    @Mapping(target = "userId", expression = "java(cardInfo.getUser().getId())")
    CardInfoCacheDTO cardInfoToCardInfoCacheDTO(CardInfo cardInfo);
}
