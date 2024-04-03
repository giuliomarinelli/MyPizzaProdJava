package backendapp.myPizza.Models.reqDTO;

import backendapp.myPizza.Models.enums.Gender;
// non la userò per questioni di tempo
public record GuestUserDTO(String firstName,

                           String lastName,

                           String email,

                           String phoneNumber) {
}
