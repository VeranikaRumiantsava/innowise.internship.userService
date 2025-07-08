package org.innowise.internship.userservice.UserService.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with ID " + id.toString() + " not found");
    }

    public UserNotFoundException(String email) {
        super("User with email: " + email + " not found");
    }
}
