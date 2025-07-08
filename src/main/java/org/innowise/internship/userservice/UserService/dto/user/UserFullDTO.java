package org.innowise.internship.userservice.UserService.dto.user;

import java.time.LocalDate;
import java.util.List;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoFullDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@Setter
@Getter
public class UserFullDTO {
    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private List<CardInfoFullDTO> cards;
}
