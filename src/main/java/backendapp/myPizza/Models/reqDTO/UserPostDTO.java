package backendapp.myPizza.Models.reqDTO;

import backendapp.myPizza.Models.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserPostDTO(
        @NotBlank(message = "'firstName' is required and must not be empty")
        String firstName,
        @NotBlank(message = "'lastName' is required and must not be empty")
        String lastName,
        @NotBlank(message = "'email' is required and must not be empty")
        @Email(regexp = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "malformed 'email' field")
        String email,
        @NotBlank(message = "'firstName' is required and must not be empty")
        String phoneNumber,
        @NotBlank(message = "'firstName' is required and must not be empty")
        String password,
        @NotNull(message = "'gender' is required")
        Gender gender,
        @NotNull(message = "'address' is required")
        AddressDTO address


) {
}
