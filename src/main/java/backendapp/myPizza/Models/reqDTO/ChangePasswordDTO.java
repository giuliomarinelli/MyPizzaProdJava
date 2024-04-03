package backendapp.myPizza.Models.reqDTO;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDTO(
        @NotBlank(message = "'oldPassword' is required")
        String oldPassword,
        @NotBlank(message = "'newPassword' is required")
        String newPassword
) {}
