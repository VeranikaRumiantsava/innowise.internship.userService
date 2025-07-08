package org.innowise.internship.userservice.UserService.services;

import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCreateDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoFullDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.CardInfo;
import org.innowise.internship.userservice.UserService.entities.User;
import org.innowise.internship.userservice.UserService.exceptions.CardNotFoundException;
import org.innowise.internship.userservice.UserService.exceptions.UserAlreadyHasTheCardWithTheSameNumberException;
import org.innowise.internship.userservice.UserService.exceptions.UserNotFoundException;
import org.innowise.internship.userservice.UserService.mappers.CardInfoMapper;
import org.innowise.internship.userservice.UserService.repositories.CardInfoRepository;
import org.innowise.internship.userservice.UserService.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final CardInfoMapper cardInfoMapper;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public CardInfoFullDTO createCard(CardInfoCreateDTO cardInfoCreateDTO) {
        validateUserDoesNotHaveCard(cardInfoCreateDTO.getUserId(), cardInfoCreateDTO.getNumber());

        User user = userRepository.findById(cardInfoCreateDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException(cardInfoCreateDTO.getUserId()));

        cacheUserEvict(user.getId());

        CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo((cardInfoCreateDTO));
        cardInfo.setUser(user);

        cardInfo = cardInfoRepository.save(cardInfo);

        return cardInfoMapper.cardInfoToCardInfoFullDTO(cardInfo);
    }

    public CardInfoFullDTO getCardById(Long id) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        return cardInfoMapper.cardInfoToCardInfoFullDTO(cardInfo);
    }

    public List<CardInfoFullDTO> getCardsByIds(List<Long> ids) {
        return cardInfoRepository.findAllByIdIn(ids).stream()
                .map(cardInfoMapper::cardInfoToCardInfoFullDTO)
                .toList();
    }

    @Transactional
    public CardInfoFullDTO updateCard(Long id, CardInfoUpdateDTO cardInfoUpdateDTO) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        cacheUserEvict(cardInfo.getUser().getId());

        validateUserDoesNotHaveCard(cardInfo.getUser().getId(), cardInfoUpdateDTO.getNumber());

        cardInfoMapper.updateCardInfoFromCardInfoUpdateDTO(cardInfoUpdateDTO, cardInfo);

        return cardInfoMapper.cardInfoToCardInfoFullDTO(cardInfoRepository.save(cardInfo));
    }

    @Transactional
    public void deleteCardById(Long id) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));

        cacheUserEvict(cardInfo.getUser().getId());

        cardInfoRepository.deleteById(id);
    }

    private void validateUserDoesNotHaveCard(Long id, String number) {
        if (cardInfoRepository.existsByUserIdAndNumber(id, number)) {
            throw new UserAlreadyHasTheCardWithTheSameNumberException(number);
        }
    }

    @CacheEvict(value = "users", key = "#id")
    public void cacheUserEvict(Long id){
    }
}
