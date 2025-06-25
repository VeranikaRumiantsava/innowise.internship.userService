package org.innowise.internship.userservice.UserService.services;

import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserFullDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserUpdateDTO;
import org.innowise.internship.userservice.UserService.entities.User;
import org.innowise.internship.userservice.UserService.mappers.UserMapper;
import org.innowise.internship.userservice.UserService.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserFullDTO createUser(UserCreateDTO userCreateDTO) {
        if (userCreateDTO == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        User user = userRepository.save(userMapper.userCreateDTOToUser(userCreateDTO));

        return userMapper.userToUserFullDTO(user);
    }

    public UserFullDTO getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User with this id doesn't exist"));
        return userMapper.userToUserFullDTO(user);
    }

    public List<UserFullDTO> getUsersByIdIn(List<Long> ids) {
        return userRepository.findAllByIdIn(ids)
                .stream()
                .map(userMapper::userToUserFullDTO)
                .toList();
    }

    public UserFullDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User with this id doesn't exist"));
        return userMapper.userToUserFullDTO(user);
    }

    @Transactional
    public UserFullDTO updateUser(UserUpdateDTO userUpdateDTO) throws InstanceNotFoundException {
        if (userUpdateDTO == null) {
            throw new IllegalArgumentException("userUpdateDTO cannot be null");
        }

        User user = userRepository.findById(userUpdateDTO.getId()).orElseThrow(() -> new InstanceNotFoundException("User with this id doesn't exist"));

        userMapper.updateUserFromUserUpdateDTO(userUpdateDTO,user);
        return userMapper.userToUserFullDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User with id  " + id.toString() + " doesn't exist");
        }
        userRepository.deleteById(id);
    }
}
