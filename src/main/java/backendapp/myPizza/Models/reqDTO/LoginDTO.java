package backendapp.myPizza.Models.reqDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginDTO(

        @Email(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "malformed 'email' field")
        @NotBlank(message = "'email' is required and must not be empty")
        String email,

        @NotBlank(message = "'password' is required and must not be empty")
        String password,

        @NotNull
        Boolean restore




) {}
