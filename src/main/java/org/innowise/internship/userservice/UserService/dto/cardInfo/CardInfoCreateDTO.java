package org.innowise.internship.userservice.UserService.dto.cardInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@Setter
@Getter
public class CardInfoCreateDTO {

    @NotNull(message = "User ID must not be null")
    private Long userId;

    @NotNull(message = "Number card must not be null")
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
    private String number;

    @NotBlank(message = "Holder name must not be blank")
    @Pattern(regexp = "^[A-Za-z\\s-]+$", message = "Holder name must contain only letters, spaces or hyphens")
    private String holder;

    @NotNull(message = "Expiration date must not be null")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiration date must be in MM/YY format")
    private String expirationDate;
}
