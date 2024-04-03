package backendapp.myPizza.Models.resDTO;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Data
public class ErrorRes {
    private int statusCode;
    private Timestamp timestamp;
    private String error;
    private String message;

    public ErrorRes(String error, String message, HttpStatus httpStatus) {
        this.error = error;
        this.message = message;
        statusCode = httpStatus.value();
        timestamp = Timestamp.valueOf(LocalDateTime.now());
    }
}
