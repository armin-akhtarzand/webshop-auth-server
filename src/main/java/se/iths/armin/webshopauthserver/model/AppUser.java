package se.iths.armin.webshopauthserver.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import se.iths.armin.webshopauthserver.model.enums.UserRole;

@Entity
@Getter
@Setter
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.ROLE_USER;


}
