package se.iths.armin.webshopauthserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.webshopauthserver.dto.LoginRequestDto;
import se.iths.armin.webshopauthserver.dto.SessionResponseDto;
import se.iths.armin.webshopauthserver.dto.TokenResponseDto;
import se.iths.armin.webshopauthserver.security.AuthService;

import java.util.List;

import java.util.Map;

/**
 * Controller för inloggning samt token funktioner
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    /**
     * Tar emot inloggningsuppgifter och anropar AuthService för autentisering.
     *
     * @param loginRequestDto användarens mejladress och lösenord
     * @return ett svar med JWT-token, giltighetstid och användaruppgifter
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    /**
     * Returnerar den publika nyckeln som andra tjänster använder
     * för att verifiera JWT-token.
     *
     * @return den publika nyckeln i JWKS-format
     */
    @GetMapping("/jwks")
    public ResponseEntity<Map<String, Object>> publicJwks() {
        return ResponseEntity.ok(authService.publicJwkSet());
    }

    /**
     * Hämtar användaruppgifter från den JWT som Spring Security har verifierat.
     *
     * @param jwt den verifierade JWT:n för den inloggade användaren
     * @return användarens identitet, roller och resterande giltighetstid i sekunder
     */
    @GetMapping("/me")
    public ResponseEntity<SessionResponseDto> me(@AuthenticationPrincipal Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");

        SessionResponseDto response = new SessionResponseDto(
                authService.remainingSeconds(jwt),
                jwt.getSubject(),
                roles == null ? List.of() : roles

        );
        return ResponseEntity.ok(response);
    }


}
