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
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
public class CardInfoControllerIT extends BaseIT {

    @Autowired
    private CardInfoMapper cardInfoMapper;

    @Autowired
    private CardInfoRepository cardInfoRepository;

    private CardInfoCreateDTO createCardInfoCreateDTO(Long userId, String number, String holder, String expirationDate) {
        CardInfoCreateDTO cardInfoCreateDTO = new CardInfoCreateDTO();
        cardInfoCreateDTO.setUserId(userId);
        cardInfoCreateDTO.setNumber(number);
        cardInfoCreateDTO.setHolder(holder);
        cardInfoCreateDTO.setExpirationDate(expirationDate);
        return cardInfoCreateDTO;
    }

    private CardInfoUpdateDTO createCardInfoUpdateDTO(String number, String holder, String expirationDate) {
        CardInfoUpdateDTO cardInfoUpdateDTO = new CardInfoUpdateDTO();
        cardInfoUpdateDTO.setNumber(number);
        cardInfoUpdateDTO.setHolder(holder);
        cardInfoUpdateDTO.setExpirationDate(expirationDate);
        return cardInfoUpdateDTO;
    }

    private User user = new User();

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

            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "1111222233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
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

            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "1112233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createCardInfoShouldReturnStatus409ConflictWhenUserAlreadyHasSameCard() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "1111222233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDTO)));

            mockMvc.perform(post("/cardinfo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isConflict());
        }

        @Test
        void createCardInfoShouldReturnStatus404NotFoundWhenUserDoesNotExists() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId() + 1, "1111222233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void createCardInfoShouldReturnStatus201CreatedWhenUserAddTwoDifferentCards() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "1111222233334444", "V R", "12/25");
            CardInfoCreateDTO createDTO2 = createCardInfoCreateDTO(user.getId(), "1111222233335555", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").value(user.getId()))
                    .andExpect(jsonPath("$.number").value("1111222233334444"));

            mockMvc.perform(post("/cardinfo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO2)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userId").value(user.getId()))
                    .andExpect(jsonPath("$.number").value("1111222233335555"));
        }
    }

    @Nested
    class GetCardInfoTests {

        private CardInfo savedCardInfo = new CardInfo();

        @BeforeEach
        void setUpBeforeGetTests() {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "1111222233334444", "V R", "12/25");

            CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO);
            cardInfo.setUser(user);
            savedCardInfo = cardInfoRepository.save(cardInfo);
        }

        @Test
        void getCardInfoByIdShouldReturnStatus200OkAndCardInfo() throws Exception {
            mockMvc.perform(get("/cardinfo/{id}", savedCardInfo.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(user.getId()))
                    .andExpect(jsonPath("$.number").value("1111222233334444"))
                    .andExpect(jsonPath("$.holder").value("V R"))
                    .andExpect(jsonPath("$.expirationDate").value("12/25"));

        }

        @Test
        void getCardInfoByIdShouldReturnStatus404NotFoundWhenIdDoesNotExist() throws Exception {
            mockMvc.perform(get("/cardinfo/{id}", 1L))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getCardInfoByIdsShouldReturnStatus200OkAndCardInfoList() throws Exception {
            CardInfoCreateDTO createDTO2 = createCardInfoCreateDTO(user.getId(), "1111222233335555", "V R", "12/25");

            CardInfo cardInfo2 = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO2);
            cardInfo2.setUser(user);

            CardInfo saved2 = cardInfoRepository.save(cardInfo2);

            mockMvc.perform(get("/cardinfo/ids")
                            .param("ids", savedCardInfo.getId() + "," + saved2.getId())
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].number").value("1111222233334444"))
                    .andExpect(jsonPath("$[1].number").value("1111222233335555"));
        }

        @Test
        void getCardInfoByIdsShouldReturnStatus200OkAndCardInfoListWhenSomeCardInfoIdsInvalid() throws Exception {
            mockMvc.perform(get("/cardinfo/ids")
                            .param("ids", savedCardInfo.getId() + "," + 78)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].number").value("1111222233334444"));
        }
    }

    @Nested
    class UpdateCardInfoTests {

        private CardInfo savedCardInfo = new CardInfo();

        @BeforeEach
        void setUpBeforeGetTests() {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "1111222233334444", "V R", "12/25");

            CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO);
            cardInfo.setUser(user);
            savedCardInfo = cardInfoRepository.save(cardInfo);
        }

        @Test
        void updateCardInfoShouldReturnStatus200OkAndUpdatedCardInfo() throws Exception {
            CardInfoUpdateDTO updateDTO = createCardInfoUpdateDTO("1111222233338844", "K R", "10/25");

            mockMvc.perform(patch("/cardinfo/{id}", savedCardInfo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number").value("1111222233338844"))
                    .andExpect(jsonPath("$.holder").value("K R"))
                    .andExpect(jsonPath("$.expirationDate").value("10/25"));
        }

        @Test
        void updateCardInfoShouldReturnStatus200OkAndUpdatedCardWhenUpdateCardPartially() throws Exception {
            CardInfoUpdateDTO updateDTO = createCardInfoUpdateDTO(null, "K R", null);

            mockMvc.perform(patch("/cardinfo/{id}", savedCardInfo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.number").value("1111222233334444"))
                    .andExpect(jsonPath("$.holder").value("K R"));
        }

        @Test
        void updateCardInfoShouldReturnStatus400BadRequestWhenUpdateCardWithInvalidData() throws Exception {
            CardInfoUpdateDTO updateDTO = createCardInfoUpdateDTO("111122223844", "K R", "10/25");

            mockMvc.perform(patch("/cardinfo/{id}", savedCardInfo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void updateCardInfoShouldReturnStatus409ConflictWhenUserAlreadyHasCardWhitSameNumber() throws Exception {
            CardInfoCreateDTO createDTO2 = createCardInfoCreateDTO(user.getId(), "1111222233335555", "V R", "12/25");

            CardInfo cardInfo2 = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO2);
            cardInfo2.setUser(user);

            cardInfoRepository.save(cardInfo2);

            CardInfoUpdateDTO updateDTO = createCardInfoUpdateDTO("1111222233335555", null, null);

            mockMvc.perform(patch("/cardinfo/{id}", savedCardInfo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isConflict());
        }

        @Test
        void updateCardInfoShouldReturnStatus404NotFoundWhenIdUpdateCardInfoDoesNotExist() throws Exception {
            CardInfoUpdateDTO updateDTO = createCardInfoUpdateDTO("1111222233334444", "K R", "10/25");

            mockMvc.perform(patch("/cardinfo/{id}", savedCardInfo.getId() + 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteCardTests {

        private CardInfo savedCardInfo = new CardInfo();

        @BeforeEach
        void setUpBeforeDeleteTests() {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "1111222233334444", "V R", "12/25");

            CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO);
            cardInfo.setUser(user);
            savedCardInfo = cardInfoRepository.save(cardInfo);
        }

        @Test
        void deleteCardByIdShouldReturnStatus204NoContent() throws Exception {
            mockMvc.perform(delete("/cardinfo/{id}", savedCardInfo.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteCardInfoByIdShouldReturnStatus404NotFoundWhenIdDoesNotExists() throws Exception {
            mockMvc.perform(delete("/cardinfo/{id}", savedCardInfo.getId() + 1))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CacheTests {

        @Autowired
        private StringRedisTemplate stringRedisTemplate;

        private CardInfo savedCardInfo = new CardInfo();

        @BeforeEach
        void setUpBeforeCacheTests() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "1111222233334444", "V R", "12/25");

            CardInfo cardInfo = cardInfoMapper.cardInfoCreateDTOToCardInfo(createDTO);
            cardInfo.setUser(user);
            savedCardInfo = cardInfoRepository.save(cardInfo);

            mockMvc.perform(get("/user/{id}", user.getId()))
                    .andExpect(status().isOk());

        }

        @Test
        void createCardShouldEvictUserInCache() throws Exception {
            CardInfoCreateDTO createDTO = createCardInfoCreateDTO(user.getId(), "0000222233334444", "V R", "12/25");

            mockMvc.perform(post("/cardinfo")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated());

            Assertions.assertFalse(stringRedisTemplate.hasKey("users::" + user.getId()));
        }

        @Test
        void updateCardShouldEvictUserInCache() throws Exception {
            CardInfoUpdateDTO updateDTO = createCardInfoUpdateDTO(null, "Ver", null);

            mockMvc.perform(patch("/cardinfo/{id}", savedCardInfo.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk());

            Assertions.assertFalse(stringRedisTemplate.hasKey("users::" + user.getId()));
        }

        @Test
        void deleteCardShouldEvictUserInCache() throws Exception {
            mockMvc.perform(delete("/cardinfo/{id}", savedCardInfo.getId()))
                    .andExpect(status().isNoContent());

            Assertions.assertFalse(stringRedisTemplate.hasKey("users::" + user.getId()));
        }
    }
}

