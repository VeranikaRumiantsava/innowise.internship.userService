package org.innowise.internship.userservice.UserService.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.innowise.internship.userservice.UserService.dto.user.UserCreateDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserFullDTO;
import org.innowise.internship.userservice.UserService.dto.user.UserUpdateDTO;
import org.innowise.internship.userservice.UserService.services.UserService;

import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserFullDTO> createUser(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        UserFullDTO userFullDTO = userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userFullDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserFullDTO> getUserById(@PathVariable Long id) {
        UserFullDTO userFullDTO = userService.getUserById(id);
        return ResponseEntity.ok(userFullDTO);
    }

    @GetMapping("/email")
    public ResponseEntity<UserFullDTO> getUserByEmail(@RequestParam String email) {
        UserFullDTO userFullDTO = userService.getUserByEmail(email);
        return ResponseEntity.ok(userFullDTO);
    }

    @GetMapping("/ids")
    public ResponseEntity<List<UserFullDTO>> getUserListByIds(@RequestParam List<Long> ids) {
        List<UserFullDTO> users = userService.getUsersByIdIn(ids);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserFullDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        UserFullDTO userFullDTO = userService.updateUserById(id, userUpdateDTO);
        return ResponseEntity.ok(userFullDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
