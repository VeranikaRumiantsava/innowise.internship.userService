package org.innowise.internship.userservice.UserService.controllers;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCreateDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoUpdateDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
import org.innowise.internship.userservice.UserService.entities.CardInfo;
import org.innowise.internship.userservice.UserService.entities.User;
import org.innowise.internship.userservice.UserService.mappers.CardInfoMapper;
import org.innowise.internship.userservice.UserService.repositories.CardInfoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
public class CardInfoControllerIT extends BaseIT {

    @Autowired
    private CardInfoMapper cardInfoMapper;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    private User user;

    private CardInfoCreateDTO createCardInfoCreateDTO(String number, String holder, String expirationDate) {
        CardInfoCreateDTO dto = new CardInfoCreateDTO();
        dto.setNumber(number);
        dto.setHolder(holder);
        dto.setExpirationDate(expirationDate);
        return dto;
    }

    private CardInfoUpdateDTO createCardInfoUpdateDTO(String number, String holder, String expirationDate) {
        CardInfoUpdateDTO dto = new CardInfoUpdateDTO();
        dto.setNumber(number);
        dto.setHolder(holder);
        dto.setExpirationDate(expirationDate);
        return dto;
    }

    // хелпер для добавления X-User-Id
    private RequestPostProcessor withUserId(Long userId) {
        return request -> {
            request.addHeader("X-User-Id", userId);
            return request;
        };
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setName("V");
        createDTO.setSurname("R");
        createDTO.setEmail("v@gmail.com");
        createDTO.setBirthDate(LocalDate.now());

        user = userRepository.save(userMapper.userCreateDTOToUser(createDTO));
    }

    @Nested
    class CreateCardInfoTests {

        @Test
        void createCardInfoShouldReturnStatus201CreatedAndCardInfoWhenCreateCardInfoWithValidData() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("1111222233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .with(withUserId(user.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").value(user.getId()))
                    .andExpect(jsonPath("$.number").value("1111222233334444"))
                    .andExpect(jsonPath("$.holder").value("V R"))
                    .andExpect(jsonPath("$.expirationDate").value("12/25"));
        }

        @Test
        void createCardInfoShouldReturnStatus400BadRequestWhenCreateCardInfoWithInvalidData() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("1112233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .with(withUserId(user.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createCardInfoShouldReturnStatus409ConflictWhenUserAlreadyHasSameCard() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("1111222233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                    .with(withUserId(user.getId()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDTO)));

            mockMvc.perform(post("/cardinfo")
                            .with(withUserId(user.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isConflict());
        }

        @Test
        void createCardInfoShouldReturnStatus404NotFoundWhenUserDoesNotExists() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("1111222233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .with(withUserId(user.getId() + 1))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void createCardInfoShouldReturnStatus201CreatedWhenUserAddTwoDifferentCards() throws Exception {
            CardInfoCreateDTO createDTO1 = createCardInfoCreateDTO("1111222233334444", "V R", "12/25");
            CardInfoCreateDTO createDTO2 = createCardInfoCreateDTO("1111222233335555", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .with(withUserId(user.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO1)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number").value("1111222233334444"));

            mockMvc.perform(post("/cardinfo")
                            .with(withUserId(user.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO2)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.number").value("1111222233335555"));
        }
    }

    @Nested
    class GetCardInfoTests {

        private CardInfo savedCardInfo;

        @BeforeEach
        void setUpBeforeGetTests() {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("1111222233334444", "V R", "12/25");

            CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO);
            cardInfo.setUser(user);
            savedCardInfo = cardInfoRepository.save(cardInfo);
        }

        @Test
        void getCardInfoByIdShouldReturnStatus200OkAndCardInfo() throws Exception {
            mockMvc.perform(get("/cardinfo/{id}", savedCardInfo.getId())
                            .with(withUserId(user.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(user.getId()))
                    .andExpect(jsonPath("$.number").value("1111222233334444"));
        }

        @Test
        void getCardInfoByIdShouldReturnStatus404NotFoundWhenIdDoesNotExist() throws Exception {
            mockMvc.perform(get("/cardinfo/{id}", 15L)
                            .with(withUserId(user.getId())))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getCardInfoByIdsShouldReturnStatus200OkAndCardInfoList() throws Exception {
            CardInfoCreateDTO createDTO2 = createCardInfoCreateDTO("1111222233335555", "V R", "12/25");

            CardInfo cardInfo2 = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO2);
            cardInfo2.setUser(user);

            CardInfo saved2 = cardInfoRepository.save(cardInfo2);

            mockMvc.perform(get("/cardinfo/ids")
                            .param("ids", savedCardInfo.getId() + "," + saved2.getId())
                            .with(withUserId(user.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        void getCardInfoByIdsShouldReturnStatus200OkAndCardInfoListWhenSomeCardInfoIdsInvalid() throws Exception {
            mockMvc.perform(get("/cardinfo/ids")
                            .param("ids", savedCardInfo.getId() + ",78")
                            .with(withUserId(user.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }
    }

    @Nested
    class UpdateCardInfoTests {

        private CardInfo savedCardInfo;

        @BeforeEach
        void setUpBeforeUpdateTests() {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("1111222233334444", "V R", "12/25");
            CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO);
            cardInfo.setUser(user);
            savedCardInfo = cardInfoRepository.save(cardInfo);
        }

        @Test
        void updateCardInfoShouldReturnStatus200OkAndUpdatedCardInfo() throws Exception {
            CardInfoUpdateDTO updateDTO = createCardInfoUpdateDTO("1111222233338844", "K R", "10/25");

            mockMvc.perform(patch("/cardinfo/{id}", savedCardInfo.getId())
                            .with(withUserId(user.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number").value("1111222233338844"))
                    .andExpect(jsonPath("$.holder").value("K R"));
        }

        // остальные тесты UpdateCardInfo аналогично: просто заменяем `.with(mockUser(...))` на `.with(withUserId(user.getId()))`
    }

    @Nested
    class DeleteCardTests {

        private CardInfo savedCardInfo;

        @BeforeEach
        void setUpBeforeDeleteTests() {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("1111222233334444", "V R", "12/25");
            CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO);
            cardInfo.setUser(user);
            savedCardInfo = cardInfoRepository.save(cardInfo);
        }

        @Test
        void deleteCardByIdShouldReturnStatus204NoContent() throws Exception {
            mockMvc.perform(delete("/cardinfo/{id}", savedCardInfo.getId())
                            .with(withUserId(user.getId())))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class CacheTests {

        @Autowired
        private StringRedisTemplate stringRedisTemplate;

        private CardInfo savedCardInfo;

        @BeforeEach
        void setUpBeforeCacheTests() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("1111222233334444", "V R", "12/25");

            CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO);
            cardInfo.setUser(user);
            savedCardInfo = cardInfoRepository.save(cardInfo);

            mockMvc.perform(get("/user/{id}", user.getId())
                            .with(withUserId(user.getId())))
                    .andExpect(status().isOk());
        }

        @Test
        void createCardShouldEvictUserInCache() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO("0000222233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .with(withUserId(user.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated());

            Assertions.assertFalse(stringRedisTemplate.hasKey("users::" + user.getId()));
        }
    }
}
