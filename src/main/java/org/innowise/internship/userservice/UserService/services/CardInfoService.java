package org.innowise.internship.userservice.UserService.services;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCreateDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoFullDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.CardInfo;
import org.innowise.internship.userservice.UserService.mappers.CardInfoMapper;
import org.innowise.internship.userservice.UserService.repositories.CardInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final CardInfoMapper cardInfoMapper;

    @Autowired
    public CardInfoService(CardInfoRepository cardInfoRepository, CardInfoMapper cardInfoMapper) {
        this.cardInfoRepository = cardInfoRepository;
        this.cardInfoMapper = cardInfoMapper;
    }

    public CardInfoFullDTO createCard(CardInfoCreateDTO cardInfoCreateDTO) {
        if (cardInfoCreateDTO == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        CardInfo cardInfo = cardInfoRepository.save(cardInfoMapper.cardInfoCreateDTOToCardInfo((cardInfoCreateDTO)));

        return cardInfoMapper.cardInfoToCardInfoFullDTO(cardInfo);
    }

    public CardInfoFullDTO getCardById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        CardInfo cardInfo = cardInfoRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card with this id doesn't exist"));
        return cardInfoMapper.cardInfoToCardInfoFullDTO(cardInfo);
    }

    public List<CardInfoFullDTO> getCardsByIds(List<Long> ids) {
        return cardInfoRepository.findAllByIdIn(ids)
                .stream()
                .map(cardInfoMapper::cardInfoToCardInfoFullDTO)
                .toList();
    }

    @Transactional
    public CardInfoFullDTO updateCard(CardInfoUpdateDTO cardInfoUpdateDTO) throws InstanceNotFoundException {
        if (cardInfoUpdateDTO == null) {
            throw new IllegalArgumentException("cardInfoUpdateDTO cannot be null");
        }

        CardInfo cardInfo = cardInfoRepository
                .findById(cardInfoUpdateDTO.getId())
                .orElseThrow(() -> new InstanceNotFoundException("Card with this id doesn't exist"));

        cardInfoMapper.updateCardInfoFromCardInfoUpdateDTO(cardInfoUpdateDTO, cardInfo);
        return cardInfoMapper.cardInfoToCardInfoFullDTO(cardInfoRepository.save(cardInfo));
    }

    @Transactional
    public void deleteCardById(Long id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new IllegalArgumentException("Card with id  " + id.toString() + " doesn't exist");
        }
        cardInfoRepository.deleteById(id);
    }
}
