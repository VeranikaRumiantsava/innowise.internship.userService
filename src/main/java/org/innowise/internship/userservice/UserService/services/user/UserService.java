package org.innowise.internship.userservice.UserService.services.user;

import java.util.List;

import org.innowise.internship.userservice.UserService.dto.user.UserResponseDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
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
    private final UserCacheService userCacheService;

    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {

        if (userRepository.findByEmail(userCreateDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("User with email " + userCreateDTO.getEmail() + " already exists");
        }

        User user = userRepository.save(userMapper.userCreateDTOToUser(userCreateDTO));

        return userMapper.userToUserResponseDTO(user);
    }


    public UserResponseDTO getUserById(Long id) {
        return userMapper.userCacheDTOToUserResponseDTO(userCacheService.getUserById(id));
    }

    public List<UserResponseDTO> getUsersByIdIn(List<Long> ids) {
        return userRepository.findAllByIdIn(ids).stream()
                .map(userMapper::userToUserResponseDTO)
                .toList();
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email: " + email + " not found"));

        return userMapper.userToUserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUserById(Long id, UserUpdateDTO userUpdateDTO) {
        return userMapper.userCacheDTOToUserResponseDTO(userCacheService.updateUserById(id,userUpdateDTO));
    }

   @Transactional
    public void deleteUserById(Long id) {
        userCacheService.deleteUserById(id);
    }
}
