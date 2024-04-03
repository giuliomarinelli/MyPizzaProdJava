package backendapp.myPizza.Models.reqDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressDTO(
        @NotBlank(message = "'road' field is required and must not be empty")
        String road,
        @NotBlank(message = "'civic' field is required and must not be empty")
        String civic,
        @NotBlank(message = "'city' field is required and must not be empty")
        String city,
        @NotBlank(message = "'province' field is required and must not be empty")
        @Pattern(regexp = "^[A-Z]{2}$", message = "'province' field must have exactly 2 capital letters")
        String province
) {}
