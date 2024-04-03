package backendapp.myPizza.Models.resDTO;

import backendapp.myPizza.Models.enums.UserScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthoritiesRes {
    private List<UserScope> authorities;
}
