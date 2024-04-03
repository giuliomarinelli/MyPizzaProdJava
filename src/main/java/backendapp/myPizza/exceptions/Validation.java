package backendapp.myPizza.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import javax.naming.Binding;
import java.util.stream.Collectors;

public class Validation {

    public static void validate(BindingResult validation) throws BadRequestException {
        if (validation.hasErrors())
            throw new BadRequestException(messages(validation));
    }

    private static String messages(BindingResult validation) {
        return validation.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
    }
}
