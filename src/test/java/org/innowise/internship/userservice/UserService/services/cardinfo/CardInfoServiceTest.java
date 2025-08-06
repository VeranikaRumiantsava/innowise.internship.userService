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
        createDTO.setNumber("1234123412341234");

        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        CardInfo cardInfo = new CardInfo();
        CardInfo savedCard = new CardInfo();
        CardInfoResponseDTO responseDTO = new CardInfoResponseDTO();

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(cardInfoRepository.existsByUserIdAndNumber(userId, "1234123412341234")).thenReturn(false);
        Mockito.when(cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO)).thenReturn(cardInfo);
        Mockito.when(cardInfoRepository.save(cardInfo)).thenReturn(savedCard);
        Mockito.when(cardInfoMapper.cardInfoToCardInfoResponseDTO(savedCard)).thenReturn(responseDTO);

        CardInfoResponseDTO result = cardInfoService.createCard(createDTO, userId);

        Assertions.assertEquals(responseDTO, result);
        Mockito.verify(userCacheService).cacheEvictUserById(userId);
    }

    @Test
    void createCardShouldThrowWhenUserNotFound() {
        Long userId = 1L;
        CardInfoCreateDTO createDTO = new CardInfoCreateDTO();
        createDTO.setNumber("1234567890123456");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> cardInfoService.createCard(createDTO, userId));

        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void createCardShouldThrowWhenUserHasDuplicateCard() {
        Long userId = 1L;

        CardInfoCreateDTO createDTO = new CardInfoCreateDTO();
        createDTO.setNumber("1234567890123456");

        User user = new User();
        user.setId(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(cardInfoRepository.existsByUserIdAndNumber(userId, "1234567890123456")).thenReturn(true);

        Assertions.assertThrows(UserAlreadyHasTheCardWithTheSameNumberException.class,
                () -> cardInfoService.createCard(createDTO, userId));

        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void getCardByIdShouldReturnCard() {
        Long userId = 1L;
        Long cardId = 10L;

        CardInfo card = new CardInfo();
        User user = new User();
        user.setId(userId);
        card.setUser(user);

        CardInfoResponseDTO responseDTO = new CardInfoResponseDTO();

        Mockito.when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(card));
        Mockito.when(cardInfoMapper.cardInfoToCardInfoResponseDTO(card)).thenReturn(responseDTO);

        CardInfoResponseDTO result = cardInfoService.getCardById(cardId, userId);

        Assertions.assertEquals(responseDTO, result);
    }

    @Test
    void getCardByIdShouldThrowWhenNotFound() {
        Long cardId = 1L;
        Long userId = 100L;

        Mockito.when(cardInfoRepository.findById(cardId)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNotFoundException.class,
                () -> cardInfoService.getCardById(cardId, userId));
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
        Long userId = 1L;

        CardInfoUpdateDTO updateDTO = new CardInfoUpdateDTO();
        updateDTO.setNumber("1122334455667788");

        User user = new User();
        user.setId(userId);

        CardInfo existingCard = new CardInfo();
        existingCard.setUser(user);
        existingCard.setNumber("oldNumber");

        CardInfo updatedCard = new CardInfo();
        updatedCard.setNumber("1122334455667788");
        CardInfoResponseDTO responseDTO = new CardInfoResponseDTO();

        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.of(existingCard));
        Mockito.when(cardInfoRepository.existsByUserIdAndNumber(userId, "1122334455667788")).thenReturn(false);
        Mockito.when(cardInfoRepository.save(existingCard)).thenReturn(updatedCard);
        Mockito.when(cardInfoMapper.cardInfoToCardInfoResponseDTO(updatedCard)).thenReturn(responseDTO);

        CardInfoResponseDTO result = cardInfoService.updateCard(id, updateDTO, userId);

        Assertions.assertEquals(responseDTO, result);
        Mockito.verify(cardInfoMapper).updateCardInfoFromCardInfoUpdateDTO(updateDTO, existingCard);
        Mockito.verify(userCacheService).cacheEvictUserById(userId);
    }

    @Test
    void updateCardShouldThrowWhenCardNotFound() {
        Long id = 1L;
        Long userId = 1L;
        CardInfoUpdateDTO updateDTO = new CardInfoUpdateDTO();

        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNotFoundException.class,
                () -> cardInfoService.updateCard(id, updateDTO, userId));

        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void updateCardShouldThrowWhenUserHasCardWithSameNumber() {
        Long id = 1L;
        Long userId = 1L;

        CardInfoUpdateDTO updateDTO = new CardInfoUpdateDTO();
        updateDTO.setNumber("1111222233334444");

        User user = new User();
        user.setId(userId);

        CardInfo existing = new CardInfo();
        existing.setUser(user);
        existing.setNumber("oldNumber"); // чтобы сравнение прошло и пошла проверка на дубликаты

        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.of(existing));
        Mockito.when(cardInfoRepository.existsByUserIdAndNumber(userId, "1111222233334444")).thenReturn(true);

        Assertions.assertThrows(UserAlreadyHasTheCardWithTheSameNumberException.class,
                () -> cardInfoService.updateCard(id, updateDTO, userId));

        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void deleteCardShouldDeleteWhenExists() {
        Long id = 1L;
        Long userId = 2L;

        User user = new User();
        user.setId(userId);
        CardInfo card = new CardInfo();
        card.setUser(user);

        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.of(card));

        cardInfoService.deleteCardById(id, userId);

        Mockito.verify(cardInfoRepository).deleteById(id);
        Mockito.verify(userCacheService).cacheEvictUserById(userId);
    }


    @Test
    void deleteCardShouldThrowWhenNotFound() {
        Long id = 1L;
        Long userId = 2L;

        Mockito.when(cardInfoRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(CardNotFoundException.class,
                () -> cardInfoService.deleteCardById(id, userId));

        Mockito.verifyNoInteractions(userCacheService);
    }

}
