package org.innowise.internship.userservice.UserService.dto.user;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@Setter
@Getter
public class UserShortDTO {
    private Long id;
    private String fullName;
    private LocalDate birthDate;
    private String email;
}
