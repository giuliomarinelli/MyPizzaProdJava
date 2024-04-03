package backendapp.myPizza.Models.reqDTO;

import backendapp.myPizza.Models.entities.Menu;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record MenuDTO(
        @NotNull(message = "'menuIds' list is required")
        List<UUID> menuIds
) {}
