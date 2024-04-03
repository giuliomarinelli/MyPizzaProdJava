package backendapp.myPizza.Models.reqDTO;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ManyProductsPostDTO(
        @NotNull(message = "'products' list is required")
        List<ProductDTO> products
) { }
