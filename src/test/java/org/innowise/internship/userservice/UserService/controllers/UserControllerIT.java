package org.innowise.internship.userservice.UserService.controllers;

import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.User;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
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
public class UserControllerIT extends BaseIT {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private UserCreateDTO createUserCreateDTO(String name, String surname, String email, LocalDate birthdate) {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setName(name);
        userCreateDTO.setSurname(surname);
        userCreateDTO.setEmail(email);
        userCreateDTO.setBirthDate(birthdate);
        return userCreateDTO;
    }

    private UserUpdateDTO createUserUpdateDTO(String name, String surname, String email, LocalDate birthdate) {
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setName(name);
        userUpdateDTO.setSurname(surname);
        userUpdateDTO.setEmail(email);
        userUpdateDTO.setBirthDate(birthdate);
        return userUpdateDTO;
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
    }

    @Nested
    class CreateUserTests {

        @Test
        void createUserShouldReturnStatus201CreatedAndUserWhenCreateUserWithValidData() throws Exception {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));

            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.surname").value("Rum"))
                    .andExpect(jsonPath("$.birthDate").value("1995-12-06"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.name").value("Veronica"));
        }

        @Test
        void createUserShouldReturnStatus400BadRequestWhenCreateUserWithInvalidData() throws Exception {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "testexample.com", LocalDate.of(1995, 12, 6));

            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void createUserShouldReturnStatus409ConflictWhenCreateUserWithExistingEmail() throws Exception {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));

            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    class GetUserTests {
        private User savedUser;

        @BeforeEach
        void setupBeforeGetTests() {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));
            savedUser = userRepository.save(userMapper.userCreateDTOToUser(createDTO));
        }

        @Test
        void getUserByIdShouldReturnStatus200OkAndUser() throws Exception {
            mockMvc.perform(get("/user/{id}", savedUser.getId())
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Veronica"));
        }

        @Test
        void getUserByIdShouldReturnStatus404NotFoundWhenIdDoesNotExist() throws Exception {
            mockMvc.perform(get("/user/{id}", savedUser.getId() + 1)
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getUserByEmailShouldReturnStatus200OkAndUser() throws Exception {
            mockMvc.perform(get("/user/email")
                            .param("email", savedUser.getEmail())
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Veronica"));
        }

        @Test
        void getUserByEmailShouldReturnStatus404NotFoundWhenEmailDoesNotExist() throws Exception {
            mockMvc.perform(get("/user/email")
                            .param("email", "not_exists@gmail.com")
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getUserByIdsShouldReturnStatus200OkAndUserList() throws Exception {
            UserCreateDTO createDTO2 = createUserCreateDTO("Nica", "Rum", "test2@example.com", LocalDate.of(1995, 12, 6));
            UserCreateDTO createDTO3 = createUserCreateDTO("Vero", "Rum", "test3@example.com", LocalDate.of(1995, 12, 6));

            User saved2 = userRepository.save(userMapper.userCreateDTOToUser(createDTO2));
            User saved3 = userRepository.save(userMapper.userCreateDTOToUser(createDTO3));

            mockMvc.perform(get("/user/ids")
                            .param("ids", savedUser.getId() + "," + saved2.getId() + "," + saved3.getId())
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3));
        }
    }

    @Nested
    class UpdateUserTests {

        private User savedUser;

        @BeforeEach
        void setupBeforeUpdateTests() {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));
            savedUser = userRepository.save(userMapper.userCreateDTOToUser(createDTO));
        }

        @Test
        void updateUserShouldReturnStatus200OkAndUpdatedUser() throws Exception {
            UserUpdateDTO updateDTO = createUserUpdateDTO("V", "Rom", "tes@example.com", LocalDate.of(1995, 12, 8));

            mockMvc.perform(patch("/user/{id}", savedUser.getId())
                            .with(withUserId(savedUser.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.surname").value("Rom"));
        }
    }

    @Nested
    class DeleteUserTests {

        private User savedUser;

        @BeforeEach
        void setupBeforeDeleteTests() {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));
            savedUser = userRepository.save(userMapper.userCreateDTOToUser(createDTO));
        }

        @Test
        void deleteUserByIdShouldReturnStatus204NoContent() throws Exception {
            mockMvc.perform(delete("/user/{id}", savedUser.getId())
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteUserByIdShouldReturnStatus404NotFoundWhenIdDoesNotExists() throws Exception {
            mockMvc.perform(delete("/user/{id}", savedUser.getId() + 1)
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CacheTests {

        private User savedUser;

        @BeforeEach
        void setupBeforeCacheTests() {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));
            savedUser = userRepository.save(userMapper.userCreateDTOToUser(createDTO));
        }

        @Test
        void getUserByIdShouldStoreUserInCache() throws Exception {
            mockMvc.perform(get("/user/{id}", savedUser.getId())
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isOk());

            Assertions.assertTrue(stringRedisTemplate.hasKey("users::" + savedUser.getId()));
        }

        @Test
        void updateUserByIdShouldUpdateUserInCache() throws Exception {
            UserUpdateDTO updateDTO = createUserUpdateDTO(null, null, "test2@mail.ru", null);

            mockMvc.perform(patch("/user/{id}", savedUser.getId())
                            .with(withUserId(savedUser.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk());

            Assertions.assertTrue(stringRedisTemplate.hasKey("users::" + savedUser.getId()));
        }

        @Test
        void deleteUserByIdShouldEvictCache() throws Exception {
            mockMvc.perform(get("/user/{id}", savedUser.getId())
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isOk());

            mockMvc.perform(delete("/user/{id}", savedUser.getId())
                            .with(withUserId(savedUser.getId())))
                    .andExpect(status().isNoContent());

            Assertions.assertFalse(stringRedisTemplate.hasKey("users::" + savedUser.getId()));
        }
    }
}
