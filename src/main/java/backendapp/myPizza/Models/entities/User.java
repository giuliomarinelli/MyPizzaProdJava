package backendapp.myPizza.Models.entities;

import backendapp.myPizza.Models.enums.Gender;
import backendapp.myPizza.Models.enums.UserScope;
import backendapp.myPizza.Models.enums._2FAStrategy;
import backendapp.myPizza.SocketIO.entities.Message;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String hashPassword;

    @Column(unique = true)
    private String phoneNumber;

    private Gender gender;

    private String profileImage;

    private long createdAt;

    private long lastUpdate;

    private String messagingUsername;

    @JsonIgnore
    private String guestEmail;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Address> addresses = new ArrayList<>();

    private _2FAStrategy _2FA;
//    @JsonIgnore
//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "senderUser")
//    List<Message> sentMessages;
//    @JsonIgnore
//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "recipientUser")
//    List<Message> receivedMessages;

    @JsonIgnore
    private List<UserScope> scope;

    public static User generateGuestUser(String firstName, String lastName, String email, String phoneNumber) {
        return new User(firstName, lastName, email, phoneNumber);
    }

    private User(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.guestEmail = email;
        this.phoneNumber = phoneNumber;
        scope = List.of(UserScope.GUEST);
        createdAt = System.currentTimeMillis();
        lastUpdate = System.currentTimeMillis();
        messagingUsername = firstName + " " + lastName + " (GUEST)";
    }

    public User(String firstName, String lastName, String email, String hashPassword, String phoneNumber, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashPassword = hashPassword;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        scope = List.of(UserScope.USER);
        createdAt = System.currentTimeMillis();
        lastUpdate = System.currentTimeMillis();
        messagingUsername = firstName + " " + lastName;
        profileImage = generateAvatar();
    }

    public String generateAvatar() {
        return "https://ui-avatars.com/api/?name=" + firstName + "+" + lastName;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<GrantedAuthority> authorities = new HashSet<GrantedAuthority>(scope.size());

        for (UserScope s : scope)
            authorities.add(new SimpleGrantedAuthority(s.name()));
        return authorities;
    }


    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return null;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
