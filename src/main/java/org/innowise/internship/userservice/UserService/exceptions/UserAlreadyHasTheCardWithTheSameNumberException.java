package org.innowise.internship.userservice.UserService.exceptions;

public class UserAlreadyHasTheCardWithTheSameNumberException extends RuntimeException {
    public UserAlreadyHasTheCardWithTheSameNumberException(String number) {
        super("This user already has card with number " + number);
    }
}
