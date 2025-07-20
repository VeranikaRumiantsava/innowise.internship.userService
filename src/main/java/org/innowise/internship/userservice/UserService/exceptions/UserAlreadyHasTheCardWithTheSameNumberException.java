package org.innowise.internship.userservice.UserService.exceptions;

public class UserAlreadyHasTheCardWithTheSameNumberException extends RuntimeException {
    public UserAlreadyHasTheCardWithTheSameNumberException(String message) {
        super(message);
    }
}
