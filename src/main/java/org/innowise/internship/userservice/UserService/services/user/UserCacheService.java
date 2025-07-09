package org.innowise.internship.userservice.UserService.services.user;

import org.innowise.internship.userservice.UserService.dto.user.UserUpdateDTO;
import org.innowise.internship.userservice.UserService.exceptions.EmailAlreadyExistsException;
import org.innowise.internship.userservice.UserService.exceptions.UserNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.innowise.internship.userservice.UserService.dto.user.UserCacheDTO;
import org.innowise.internship.userservice.UserService.entities.User;
import org.innowise.internship.userservice.UserService.mappers.UserMapper;
import org.innowise.internship.userservice.UserService.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserCacheService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Cacheable(value = "users", key = "#id")
    public UserCacheDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        return userMapper.userToUserCacheDTO(user);
    }

    @CachePut(value = "users", key = "#id")
    @Transactional
    public UserCacheDTO updateUserById(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));

        if (!Objects.equals(user.getEmail(), userUpdateDTO.getEmail())
                && userRepository.findByEmail(userUpdateDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User with email " + userUpdateDTO.getEmail() + " already exists");
        }

        userMapper.updateUserFromUserUpdateDTO(userUpdateDTO, user);

        userRepository.save(user);

        return userMapper.userToUserCacheDTO(user);
    }

    @CacheEvict(value = "users", key="#id")
    @Transactional
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @CacheEvict(value = "users", key="#id")
    public void cacheEvictUserById(Long id) {
    }

}
