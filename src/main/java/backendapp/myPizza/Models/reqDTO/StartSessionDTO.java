package backendapp.myPizza.Models.reqDTO;

import backendapp.myPizza.Models.enums.WorkSessionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StartSessionDTO(

        @NotNull(message = "'type' is required")
        WorkSessionType type,

        @Min(value = 0, message = "'openTime' must be a positive number")
        @NotNull(message = "'openTime' is required")
        Long openTime,

        @Min(value = 0, message = "'closeTime' must be a positive number")
        @NotNull(message = "'closeTime' is required")
        Long closeTime,

        @Min(value = 0, message = "'cookCount' must be a positive number")
        @NotNull(message = "'cookCount' is required")
        Integer cookCount,
        @Min(value = 0, message = "'openTime' must be a positive number")
        @NotNull(message = "'ridersCount' is required")
        Integer ridersCount

) {
}
