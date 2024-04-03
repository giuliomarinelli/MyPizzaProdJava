package backendapp.myPizza.Models.resDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsThereAnActiveSessionRes {
    private boolean isThereAnActiveSession;
}
