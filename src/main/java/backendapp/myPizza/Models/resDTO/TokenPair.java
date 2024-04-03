package backendapp.myPizza.Models.resDTO;

import backendapp.myPizza.Models.enums.TokenPairType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenPair {
    String accessToken;
    String refreshToken;
    TokenPairType type;
}
