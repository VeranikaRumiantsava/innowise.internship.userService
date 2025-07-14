package org.innowise.internship.userservice.UserService.services.user;

import org.innowise.internship.userservice.UserService.dto.user.UserCacheDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserResponseDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.User;
import org.innowise.internship.userservice.UserService.exceptions.EmailAlreadyExistsException;
import org.innowise.internship.userservice.UserService.exceptions.UserNotFoundException;
import org.innowise.internship.userservice.UserService.mappers.UserMapper;
import org.innowise.internship.userservice.UserService.repositories.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserCacheService userCacheService;

    @InjectMocks
    private UserService userService;

    @Test
    void createUserShouldReturnUserResponseDTO() {
        UserCreateDTO createDTO = new UserCreateDTO();
        User user = new User();
        UserResponseDTO responseDTO = new UserResponseDTO();

        Mockito.when(userMapper.userCreateDTOToUser(createDTO)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.userToUserResponseDTO(user)).thenReturn(responseDTO);

        UserResponseDTO result = userService.createUser(createDTO);

        Assertions.assertEquals(responseDTO, result);
        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void createUserShouldThrowEmailExceptionWhenEmailExists() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setEmail("email");
        User user = new User();

        Mockito.when(userRepository.findByEmail(createDTO.getEmail())).thenReturn(Optional.of(user));

        Assertions.assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createUser(createDTO));

        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository);
        Mockito.verifyNoInteractions(userCacheService);
    }

    @Test
    void getUserByIdShouldReturnUserResponseDTO() {
        Long id = 1L;
        UserCacheDTO cacheDTO = new UserCacheDTO();
        UserResponseDTO responseDTO = new UserResponseDTO();

        Mockito.when(userCacheService.getUserById(id)).thenReturn(cacheDTO);
        Mockito.when(userMapper.userCacheDTOToUserResponseDTO(cacheDTO)).thenReturn(responseDTO);

        UserResponseDTO result = userService.getUserById(id);

        Assertions.assertEquals(responseDTO, result);
    }

    @Test
    void getUsersByIdInShouldReturnList() {
        List<Long> ids = List.of(1L, 2L);
        List<User> users = List.of(new User(), new User());
        List<UserResponseDTO> dtos = List.of(new UserResponseDTO(), new UserResponseDTO());

        Mockito.when(userRepository.findAllByIdIn(ids)).thenReturn(users);
        Mockito.when(userMapper.userToUserResponseDTO(Mockito.any())).thenReturn(dtos.get(0), dtos.get(1));

        List<UserResponseDTO> result = userService.getUsersByIdIn(ids);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void getUserByEmailShouldReturnUserResponseDTO() {
        String email = "t.t@mail.com";
        User user = new User();
        UserResponseDTO dto = new UserResponseDTO();

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.userToUserResponseDTO(user)).thenReturn(dto);

        Assertions.assertEquals(dto, userService.getUserByEmail(email));
    }

    @Test
    void getUserByEmailShouldThrowWhenNotFound() {
        Mockito.when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userService.getUserByEmail("a@a.com"));
    }

    @Test
    void updateUserByIdShouldReturnResponseDTO() {
        Long id = 1L;
        UserUpdateDTO dto = new UserUpdateDTO();
        UserCacheDTO cacheDTO = new UserCacheDTO();
        UserResponseDTO responseDTO = new UserResponseDTO();

        Mockito.when(userCacheService.updateUserById(id, dto)).thenReturn(cacheDTO);
        Mockito.when(userMapper.userCacheDTOToUserResponseDTO(cacheDTO)).thenReturn(responseDTO);

        Assertions.assertEquals(responseDTO, userService.updateUserById(id, dto));
    }

    @Test
    void deleteUserByIdShouldCallCacheEvictService() {
        Long id = 1L;

        userService.deleteUserById(id);

        Mockito.verify(userCacheService).deleteUserById(id);
    }
}

