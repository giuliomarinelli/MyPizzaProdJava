package backendapp.myPizza.Models.resDTO;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
@Data
public class ConfirmRes {
    private int statusCode;
    private Timestamp timestamp;
    private String message;

    public ConfirmRes(String message, HttpStatus httpStatus) {
        this.message = message;
        statusCode = httpStatus.value();
        timestamp = Timestamp.valueOf(LocalDateTime.now());
    }
}
