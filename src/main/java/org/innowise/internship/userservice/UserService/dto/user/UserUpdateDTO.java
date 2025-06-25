package org.innowise.internship.userservice.UserService.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoUpdateDTO;

import java.time.LocalDate;
import java.util.List;

public class UserUpdateDTO {
    @NotNull(message = "ID can't be null")
    private Long id;

    private String name;

    private String surname;

    @PastOrPresent(message = "Birth date can't be in future")
    private LocalDate birthDate;

    @Email(message = "Email should be valid")
    private String email;

    private List<CardInfoUpdateDTO> cards;

    public UserUpdateDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CardInfoUpdateDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardInfoUpdateDTO> cards) {
        this.cards = cards;
    }
}
