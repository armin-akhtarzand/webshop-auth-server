package se.iths.armin.webshopauthserver.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import se.iths.armin.webshopauthserver.model.AppUser;
import se.iths.armin.webshopauthserver.repository.AppUserRepository;

/**
 * En komponent för att returnera användarens information
 */
@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;


    /**
     * Hämtar användare via mejladressen
     * Mejladress används som användarnamn vid autentisering
     *
     * @param email mejladressen som identifierar vems information som behövs
     * @return UserDetails med mejladress, hashat lösenord och behörighet (roll)
     * @throws UsernameNotFoundException ifall mejlen inte finns registrerad
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        AppUser user = appUserRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException(email));

        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }
}
