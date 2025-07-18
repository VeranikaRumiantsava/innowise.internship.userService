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

import java.time.LocalDate;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerIT extends BaseIT {

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
         private User savedUser = new User();

         @BeforeEach
         void setupBeforeGetTests() {
             UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));

             User user = userMapper.userCreateDTOToUser(createDTO);

             savedUser = userRepository.save(user);
         }

        @Test
        void getUserByIdShouldReturnStatus200OkAndUser() throws Exception {
            mockMvc.perform(get("/user/{id}", savedUser.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Veronica"))
                    .andExpect(jsonPath("$.surname").value("Rum"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.birthDate").value("1995-12-06"));
        }

        @Test
        void getUserByIdShouldReturnStatus404NotFoundWhenIdDoesNotExist() throws Exception {
            mockMvc.perform(get("/user/{id}", savedUser.getId() - 1))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getUserByEmailShouldReturnStatus200OkAndUser() throws Exception {
            mockMvc.perform(get("/user/email")
                            .param("email", savedUser.getEmail()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Veronica"));
        }

        @Test
        void getUserByEmailShouldReturnStatus404NotFoundWhenEmailDoesNotExist() throws Exception {
            mockMvc.perform(get("/user/email")
                            .param("email", "not_exists@gmail.com"))
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
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].name").value("Veronica"))
                    .andExpect(jsonPath("$[1].name").value("Nica"))
                    .andExpect(jsonPath("$[2].name").value("Vero"));
        }

        @Test
        void getUserByIdsShouldReturnStatus200OkAndUserListWhenSomeUserIdsInvalid() throws Exception {
            mockMvc.perform(get("/user/ids")
                            .param("ids", savedUser.getId() + "," + 2 + "," + 3)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("Veronica"));
        }
    }

    @Nested
    class UpdateUserTests {

        private User savedUser = new User();

        @BeforeEach
        void setupBeforeUpdateTests() {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));

            User user = userMapper.userCreateDTOToUser(createDTO);

            savedUser = userRepository.save(user);
        }

        @Test
        void updateUserShouldReturnStatus200OkAndUpdatedUser() throws Exception {
            UserUpdateDTO updateDTO = createUserUpdateDTO("V", "Rom", "tes@example.com", LocalDate.of(1995, 12, 8));

            mockMvc.perform(patch("/user/{id}", savedUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.surname").value("Rom"))
                    .andExpect(jsonPath("$.birthDate").value("1995-12-08"))
                    .andExpect(jsonPath("$.email").value("tes@example.com"))
                    .andExpect(jsonPath("$.name").value("V"));
        }

        @Test
        void updateUserShouldReturnStatus200OkAndUpdatedUserWhenUpdateUserPartially() throws Exception {
            UserUpdateDTO updateDTO = createUserUpdateDTO("V", null, null, null);

            mockMvc.perform(patch("/user/{id}", savedUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.surname").value("Rum"))
                    .andExpect(jsonPath("$.name").value("V"));
        }

        @Test
        void updateUserShouldReturnStatus400BadRequestWhenUpdateUserWithInvalidData() throws Exception {
            UserUpdateDTO updateDTO = createUserUpdateDTO("V", "Rom", "tesexample.com", LocalDate.of(1995, 12, 8));

            mockMvc.perform(patch("/user/{id}", savedUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void updateUserShouldReturnStatus409ConflictWhenUpdateUserWithExistingEmail() throws Exception {
            UserCreateDTO createDTO2 = createUserCreateDTO("V", "R", "test2@example.com", LocalDate.of(1995, 12, 6));
            userRepository.save(userMapper.userCreateDTOToUser(createDTO2));

            UserUpdateDTO updateDTO = createUserUpdateDTO("V", "Rom", "test2@example.com", LocalDate.of(1995, 12, 8));

            mockMvc.perform(patch("/user/{id}", savedUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isConflict());
        }

        @Test
        void updateUserShouldReturnStatus404NotFoundWhenIdUpdateUserDoesNotExist() throws Exception {
            UserUpdateDTO updateDTO = createUserUpdateDTO("V", "Rom", "test2@email.com", LocalDate.of(1995, 12, 8));

            mockMvc.perform(patch("/user/{id}", savedUser.getId() + 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteUserTests {
        private User savedUser = new User();

        @BeforeEach
        void setupBeforeUpdateTests() {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));

            User user = userMapper.userCreateDTOToUser(createDTO);

            savedUser = userRepository.save(user);
        }

        @Test
        void deleteUserByIdShouldReturnStatus204NoContent() throws Exception {
            mockMvc.perform(delete("/user/{id}", savedUser.getId()))
                    .andExpect(status().isNoContent());

        }

        @Test
        void deleteUserByIdShouldReturnStatus404NotFoundWhenIdDoesNotExists() throws Exception {
           mockMvc.perform(delete("/user/{id}", savedUser.getId() - 1))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class CacheTests {

        @Autowired
        private StringRedisTemplate stringRedisTemplate;

        private User savedUser = new User();

        @BeforeEach
        void setupBeforeCacheTests() {
            UserCreateDTO createDTO = createUserCreateDTO("Veronica", "Rum", "test@example.com", LocalDate.of(1995, 12, 6));

            User user = userMapper.userCreateDTOToUser(createDTO);

            savedUser = userRepository.save(user);
        }

        @Test
        void getUserByIdShouldStoreUserInCache() throws Exception {

            mockMvc.perform(get("/user/{id}", savedUser.getId()))
                    .andExpect(status().isOk());

            Mockito.verify(userCacheService, Mockito.times(1)).getUserById(savedUser.getId());

            Assertions.assertTrue(stringRedisTemplate.hasKey("users::" + savedUser.getId()));

            mockMvc.perform(get("/user/{id}", savedUser.getId()))
                    .andExpect(status().isOk());

            Mockito.verify(userCacheService, Mockito.times(1)).getUserById(savedUser.getId());
        }

        @Test
        void updateUserByIdShouldUpdateUserInCache() throws Exception {
            UserUpdateDTO updateDTO = createUserUpdateDTO(null, null, "test2@mail.ru", null);

            Assertions.assertFalse(stringRedisTemplate.hasKey("users::" + savedUser.getId()));

            mockMvc.perform(patch("/user/{id}", savedUser.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk());

            Assertions.assertTrue(stringRedisTemplate.hasKey("users::" + savedUser.getId()));
        }

        @Test
        void deleteUserByIdShouldEvictCache() throws Exception {

            mockMvc.perform(get("/user/{id}", savedUser.getId()))
                    .andExpect(status().isOk());

            Assertions.assertTrue(stringRedisTemplate.hasKey("users::" + savedUser.getId()));

            mockMvc.perform(delete("/user/{id}", savedUser.getId()))
                    .andExpect(status().is(204));

            Assertions.assertFalse(stringRedisTemplate.hasKey("users::" + savedUser.getId()));
        }
    }
}

