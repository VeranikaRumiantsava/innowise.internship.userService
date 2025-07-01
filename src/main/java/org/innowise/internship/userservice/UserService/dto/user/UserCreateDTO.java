package org.innowise.internship.userservice.UserService.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public class UserCreateDTO {
    @NotBlank(message = "Name can't be empty")
    private String name;

    private String surname;

    @NotNull(message = "Birth date can't be empty")
    @PastOrPresent(message = "Birth date can't be in future")
    private LocalDate birthDate;

    @Email(message = "Email should be valid")
    private String email;

    public UserCreateDTO() {
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }
}
