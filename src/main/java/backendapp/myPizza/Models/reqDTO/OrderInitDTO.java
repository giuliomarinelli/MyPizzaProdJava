package backendapp.myPizza.Models.reqDTO;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderInitDTO(
        @NotNull(message = "'orderSetsDTO' list is required")
        List<OrderSetDTO> orderSetsDTO
) {
}
