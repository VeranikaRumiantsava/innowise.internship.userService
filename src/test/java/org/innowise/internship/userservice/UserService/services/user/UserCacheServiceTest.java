package org.innowise.internship.userservice.UserService.services.user;

import org.innowise.internship.userservice.UserService.dto.user.UserCacheDTO;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserCacheServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserCacheService userCacheService;

    @Test
    void getUserByIdShouldReturnUserCacheDTOWhenUserExists() {
        Long id = 1L;
        User user = new User();
        UserCacheDTO dto = new UserCacheDTO();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.userToUserCacheDTO(user)).thenReturn(dto);

        UserCacheDTO result = userCacheService.getUserById(id);

        Assertions.assertEquals(dto, result);
    }

    @Test
    void getUserByIdShouldThrowExceptionWhenUserNotFound() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userCacheService.getUserById(1L));

        Mockito.verifyNoInteractions(userMapper);
    }

    @Test
    void updateUserByIdShouldUpdateAndReturnUser() {
        Long id = 1L;
        User user = new User();
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("r.r@gmail.com");
        UserCacheDTO resultDTO = new UserCacheDTO();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapper.userToUserCacheDTO(user)).thenReturn(resultDTO);

        UserCacheDTO result = userCacheService.updateUserById(id, updateDTO);

        Assertions.assertEquals(resultDTO, result);
        Mockito.verify(userMapper).updateUserFromUserUpdateDTO(updateDTO, user);
    }

    @Test
    void updateUserByIdShouldThrowWhenUserNotExists() {
        Long id = 1L;
        UserUpdateDTO updateDTO = new UserUpdateDTO();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userCacheService.updateUserById(id, updateDTO));

        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserByIdShouldThrowWhenEmailExists() {
        Long id = 1L;
        User user = new User();
        user.setEmail("old@email.com");
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("new@email.com");

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(new User()));

        Assertions.assertThrows(EmailAlreadyExistsException.class,
                () -> userCacheService.updateUserById(id, updateDTO));

        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUserById_ShouldDeleteWhenUserExists() {
        Long id = 1L;

        Mockito.when(userRepository.existsById(id)).thenReturn(true);

        userCacheService.deleteUserById(id);

        Mockito.verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUserByIdShouldThrowWhenUserNotFound() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(false);

        Assertions.assertThrows(UserNotFoundException.class,
                () -> userCacheService.deleteUserById(1L));

        Mockito.verifyNoMoreInteractions(userRepository);
    }
}

