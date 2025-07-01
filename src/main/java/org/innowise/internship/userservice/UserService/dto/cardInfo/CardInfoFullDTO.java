package org.innowise.internship.userservice.UserService.dto.cardInfo;

import org.innowise.internship.userservice.UserService.dto.user.UserFullDTO;

public class CardInfoFullDTO {
    private Long id;
    private UserFullDTO user;
    private String number;
    private String holder;
    private String expirationDate;

    public CardInfoFullDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserFullDTO getUser() {
        return user;
    }

    public void setUser(UserFullDTO user) {
        this.user = user;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
