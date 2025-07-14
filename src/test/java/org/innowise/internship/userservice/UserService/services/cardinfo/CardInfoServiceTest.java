package org.innowise.internship.userservice.UserService.services.cardinfo;

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
import org.innowise.internship.userservice.UserService.services.user.UserCacheService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserCacheService userCacheService;

    @InjectMocks
    private CardInfoService cardInfoService;

    @Test
    void createCardShouldCreateCardWhenUserExistsAndNoDuplicateNumber() {
        CardInfoCreateDTO createDTO = new CardInfoCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setNumber("1234123412341234");

        User user = new User();
        user.setId(1L);

        CardInfo cardInfo = new CardInfo();
        CardInfo savedCard = new CardInfo();
        CardInfoResponseDTO responseDTO = new CardInfoResponseDTO();

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(cardInfoRepository.existsByUserIdAndNumber(1L, "1234123412341234")).thenReturn(false);
        Mockito.when(cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO)).thenReturn(cardInfo);
        Mockito.when(cardInfoRepository.save(cardInfo)).thenReturn(savedCard);
        Mockito.when(cardInfoMapper.cardInfoToCardInfoResponseDTO(savedCard)).thenReturn(responseDTO);

        CardInfoResponseDTO result = cardInfoService.createCard(createDTO);

        Assertions.assertEquals(responseDTO, result);
        Mockito.verify(userCacheService).cacheEvictUserById(1L);
    }

    @Test
    void createCardShouldThrowWhenUserNotFound() {
        CardInfoCreateDTO createDTO = new CardInfoCreateDTO();
        createDTO.setUserId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> cardInfoService.createCard(createDTO));

        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void createCardShouldThrowWhenUserHasDuplicateCard() {
        CardInfoCreateDTO createDTO = new CardInfoCreateDTO();
        createDTO.setUserId(1L);
        createDTO.setNumber("1234123412341234");

        User user = new User();
        user.setId(1L);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(cardInfoRepository.existsByUserIdAndNumber(1L, "1234123412341234")).thenReturn(true);

        Assertions.assertThrows(UserAlreadyHasTheCardWithTheSameNumberException.class,
                () -> cardInfoService.createCard(createDTO));

        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void getCardByIdShouldReturnCard() {
        CardInfo card = new CardInfo();
        CardInfoResponseDTO responseDTO = new CardInfoResponseDTO();

        Mockito.when(cardInfoRepository.findById(1L)).thenReturn(Optional.of(card));
        Mockito.when(cardInfoMapper.cardInfoToCardInfoResponseDTO(card)).thenReturn(responseDTO);

        Assertions.assertEquals(responseDTO, cardInfoService.getCardById(1L));
    }

    @Test
    void getCardByIdShouldThrowWhenNotFound() {

        Mockito.when(cardInfoRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNotFoundException.class,
                () -> cardInfoService.getCardById(1L));
    }

    @Test
    void getCardsByIdsShouldReturnList() {
        List<Long> ids = List.of(1L, 2L);
        List<CardInfo> cards = List.of(new CardInfo(), new CardInfo());
        List<CardInfoResponseDTO> dtoList = List.of(new CardInfoResponseDTO(), new CardInfoResponseDTO());

        Mockito.when(cardInfoRepository.findAllByIdIn(ids)).thenReturn(cards);
        Mockito.when(cardInfoMapper.cardInfoToCardInfoResponseDTO(Mockito.any()))
                .thenReturn(dtoList.get(0), dtoList.get(1));

        List<CardInfoResponseDTO> result = cardInfoService.getCardsByIds(ids);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getCardsByIdsShouldReturnListEvenWhenNotAllIdsExists() {
        List<Long> ids = List.of(1L, 2L);
        List<CardInfo> cards = List.of(new CardInfo());
        List<CardInfoResponseDTO> dtoList = List.of(new CardInfoResponseDTO());

        Mockito.when(cardInfoRepository.findAllByIdIn(ids)).thenReturn(cards);
        Mockito.when(cardInfoMapper.cardInfoToCardInfoResponseDTO(cards.getFirst()))
                .thenReturn(dtoList.getFirst());

        List<CardInfoResponseDTO> result = cardInfoService.getCardsByIds(ids);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void updateCardShouldUpdateWhenCardValid() {
        Long id = 1L;
        CardInfoUpdateDTO updateDTO = new CardInfoUpdateDTO();
        updateDTO.setNumber("1122334455667788");

        User user = new User();
        user.setId(1L);

        CardInfo existingCard = new CardInfo();
        existingCard.setUser(user);

        CardInfo updatedCard = new CardInfo();
        CardInfoResponseDTO responseDTO = new CardInfoResponseDTO();

        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.of(existingCard));
        Mockito.when(cardInfoRepository.existsByUserIdAndNumber(1L, "1122334455667788")).thenReturn(false);
        Mockito.when(cardInfoRepository.save(existingCard)).thenReturn(updatedCard);
        Mockito.when(cardInfoMapper.cardInfoToCardInfoResponseDTO(updatedCard)).thenReturn(responseDTO);

        CardInfoResponseDTO result = cardInfoService.updateCard(id, updateDTO);

        Assertions.assertEquals(responseDTO, result);
        Mockito.verify(cardInfoMapper).updateCardInfoFromCardInfoUpdateDTO(updateDTO, existingCard);
        Mockito.verify(userCacheService).cacheEvictUserById(1L);
    }

    @Test
    void updateCardShouldThrowWhenCardNotFound() {
        Mockito.when(cardInfoRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNotFoundException.class,
                () -> cardInfoService.updateCard(1L, new CardInfoUpdateDTO()));
        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void updateCardShouldThrowWhenUserHasCardWithSameNumber() {
        Long id = 1L;
        CardInfoUpdateDTO updateDTO = new CardInfoUpdateDTO();
        updateDTO.setNumber("1111222233334444");

        User user = new User();
        user.setId(1L);
        CardInfo existing = new CardInfo();
        existing.setUser(user);

        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(cardInfoRepository.existsByUserIdAndNumber(1L, "1111222233334444")).thenReturn(true);

        Assertions.assertThrows(UserAlreadyHasTheCardWithTheSameNumberException.class,
                () -> cardInfoService.updateCard(id, updateDTO));
        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void deleteCardShouldDeleteWhenExists() {
        Long id = 1L;
        User user = new User();
        user.setId(2L);
        CardInfo card = new CardInfo();
        card.setUser(user);

        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.of(card));

        cardInfoService.deleteCardById(id);

        Mockito.verify(cardInfoRepository).deleteById(id);
        Mockito.verify(userCacheService).cacheEvictUserById(2L);
    }

    @Test
    void deleteCardShouldThrowWhenNotFound() {
        Long id = 1L;
        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNotFoundException.class,
                () -> cardInfoService.deleteCardById(id));

        Mockito.verifyNoInteractions(userCacheService);
    }
}
