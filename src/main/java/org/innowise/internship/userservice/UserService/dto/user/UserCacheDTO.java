package org.innowise.internship.userservice.UserService.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCacheDTO;

import java.time.LocalDate;
import java.util.List;


@NoArgsConstructor
@Setter
@Getter
public class UserCacheDTO {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private List<CardInfoCacheDTO> cards;
}
