package org.innowise.internship.userservice.UserService.dto.user;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoFullDTO;

import java.time.LocalDate;
import java.util.List;

public class UserFullDTO {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private List<CardInfoFullDTO> cards;

    public UserFullDTO() {
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

    public List<CardInfoFullDTO> getCards() {
        return cards;
    }

    public void setCards(List<CardInfoFullDTO> cards) {
        this.cards = cards;
    }
}
