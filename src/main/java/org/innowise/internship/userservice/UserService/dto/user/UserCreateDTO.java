package org.innowise.internship.userservice.UserService.dto.user;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@Setter
@Getter
public class UserCreateDTO {
    @NotBlank(message = "Name can't be empty")
    @Pattern(regexp = "^[A-Za-z\\s-]+$", message = "Name must contain only letters, spaces or hyphens")
    private String name;

    @Pattern(regexp = "^[A-Za-z\\s-]+$", message = "Surname must contain only letters, spaces or hyphens")
    private String surname;

    @NotNull(message = "Birth date can't be empty")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Email can't be empty")
    @Email(message = "Email should be valid")
    private String email;
}
