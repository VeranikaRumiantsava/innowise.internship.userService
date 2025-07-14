package org.innowise.internship.userservice.UserService.services.cardinfo;

import java.util.List;

import org.innowise.internship.userservice.UserService.services.user.UserCacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCreateDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoResponseDTO;
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
    private final UserCacheService userCacheService;

    public CardInfoResponseDTO createCard(CardInfoCreateDTO cardInfoCreateDTO) {
        User user = userRepository.findById(cardInfoCreateDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with ID " + cardInfoCreateDTO.getUserId() + " not found"));

        validateUserDoesNotHaveCard(cardInfoCreateDTO.getUserId(), cardInfoCreateDTO.getNumber());

        userCacheService.cacheEvictUserById(user.getId());

        CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo((cardInfoCreateDTO));
        cardInfo.setUser(user);

        cardInfo = cardInfoRepository.save(cardInfo);

        return cardInfoMapper.cardInfoToCardInfoResponseDTO(cardInfo);
    }

    public CardInfoResponseDTO getCardById(Long id) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + id + " not found"));

        return cardInfoMapper.cardInfoToCardInfoResponseDTO(cardInfo);
    }

    public List<CardInfoResponseDTO> getCardsByIds(List<Long> ids) {
        return cardInfoRepository.findAllByIdIn(ids).stream()
                .map(cardInfoMapper::cardInfoToCardInfoResponseDTO)
                .toList();
    }

    @Transactional
    public CardInfoResponseDTO updateCard(Long id, CardInfoUpdateDTO cardInfoUpdateDTO) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + id + " not found"));

        validateUserDoesNotHaveCard(cardInfo.getUser().getId(), cardInfoUpdateDTO.getNumber());

        userCacheService.cacheEvictUserById(cardInfo.getUser().getId());

        cardInfoMapper.updateCardInfoFromCardInfoUpdateDTO(cardInfoUpdateDTO, cardInfo);

        return cardInfoMapper.cardInfoToCardInfoResponseDTO(cardInfoRepository.save(cardInfo));
    }

    @Transactional
    public void deleteCardById(Long id) {
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + id + " not found"));

        userCacheService.cacheEvictUserById(cardInfo.getUser().getId());
        cardInfoRepository.deleteById(id);
    }

    private void validateUserDoesNotHaveCard(Long id, String number) {
        if (cardInfoRepository.existsByUserIdAndNumber(id, number)) {
            throw new UserAlreadyHasTheCardWithTheSameNumberException("This user already has card with number " + number);
        }
    }
}
