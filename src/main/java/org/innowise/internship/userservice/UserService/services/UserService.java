package org.innowise.internship.userservice.UserService.services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserFullDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.User;
import org.innowise.internship.userservice.UserService.exceptions.EmailAlreadyExistsException;
import org.innowise.internship.userservice.UserService.exceptions.UserNotFoundException;
import org.innowise.internship.userservice.UserService.mappers.UserMapper;
import org.innowise.internship.userservice.UserService.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserFullDTO createUser(UserCreateDTO userCreateDTO) {
        User user;
        try {
            user = userRepository.save(userMapper.userCreateDTOToUser(userCreateDTO));
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("email")) {
                throw new EmailAlreadyExistsException(userCreateDTO.getEmail());
            }
            throw ex;
        }
        return userMapper.userToUserFullDTO(user);
    }

    @Cacheable(value = "users", key = "#id")
    public UserFullDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.userToUserFullDTO(user);
    }

    public List<UserFullDTO> getUsersByIdIn(List<Long> ids) {
        return userRepository.findAllByIdIn(ids).stream()
                .map(userMapper::userToUserFullDTO)
                .toList();
    }

    public UserFullDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return userMapper.userToUserFullDTO(user);
    }

    @CachePut(value = "users", key = "#id")
    @Transactional
    public UserFullDTO updateUserById(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userMapper.updateUserFromUserUpdateDTO(userUpdateDTO, user);

        userRepository.save(user);
        try {
            userRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("email")) {
                throw new EmailAlreadyExistsException(user.getEmail());
            }
            throw ex;
        }
        return userMapper.userToUserFullDTO(user);
    }

    @CacheEvict(value = "users", key="#id")
    @Transactional
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
