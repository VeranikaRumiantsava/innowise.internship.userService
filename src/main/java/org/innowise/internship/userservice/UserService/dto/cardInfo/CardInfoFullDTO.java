package org.innowise.internship.userservice.UserService.dto.cardInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class CardInfoFullDTO {
    private Long id;
    private Long userId;
    private String number;
    private String holder;
    private String expirationDate;
}
