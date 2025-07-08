package org.innowise.internship.userservice.UserService.exceptions;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Long id) {
        super("Card with ID " + id.toString() + " not found");
    }
}
