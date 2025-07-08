package org.innowise.internship.userservice.UserService.dto.cardInfo;

import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@Setter
@Getter
public class CardInfoUpdateDTO {
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
    private String number;

    @Pattern(regexp = "^[A-Za-z\\s-]+$", message = "Holder name must contain only letters, spaces or hyphens")
    private String holder;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Expiration date must be in MM/YY format")
    private String expirationDate;
}
