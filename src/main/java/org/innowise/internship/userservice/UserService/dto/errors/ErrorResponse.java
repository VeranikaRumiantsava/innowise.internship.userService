package org.innowise.internship.userservice.UserService.dto.errors;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorResponse {
    final private List<String> message;
    final private String timestamp;
    final private int status;
    final private String error;

    public ErrorResponse(List<String> message, int status, String error) {
        this.message = message;
        this.timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.status = status;
        this.error = error;
    }
}
