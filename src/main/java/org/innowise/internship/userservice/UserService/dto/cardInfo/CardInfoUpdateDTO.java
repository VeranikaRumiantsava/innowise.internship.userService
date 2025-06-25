package org.innowise.internship.userservice.UserService.dto.cardInfo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.innowise.internship.userservice.UserService.entities.User;

public class CardInfoUpdateDTO {
    @NotNull(message = "Id can't be null")
    private Long id;

    @NotNull
    private User user;

    @Size(min = 16, max = 16)
    @NotNull
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
    private String number;

    @NotNull
    private String holder;

    @Size(max = 5)
    @NotNull
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiration date must be in MM/YY format")
    private String expirationDate;

    public CardInfoUpdateDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
