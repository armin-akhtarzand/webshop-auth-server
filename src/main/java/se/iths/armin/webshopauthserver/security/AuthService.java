package se.iths.armin.webshopauthserver.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import se.iths.armin.webshopauthserver.dto.LoginRequestDto;
import se.iths.armin.webshopauthserver.dto.TokenResponseDto;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * Service som hanterar bl.a inloggning samt skapa & signera token
 */
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final KeyPair keyPair;
    private final String jwtIssuer;
    private final long jwtExpirationMinutes;
    private final String jwtKeyId;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            KeyPair keyPair,
            @Value("${JWT_ISSUER:http://localhost:8080}") String jwtIssuer,
            @Value("${app.jwt.expiration-minutes}") long jwtExpirationMinutes,
            @Value("${app.jwt.key-id}") String jwtKeyId
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.keyPair = keyPair;
        this.jwtIssuer = jwtIssuer;
        this.jwtExpirationMinutes = jwtExpirationMinutes;
        this.jwtKeyId = jwtKeyId;
    }

    /**
     * Verifierar användarens inloggningsuppgifter.
     * Bygger en JWT med användarens mejladress, roller och giltighetstid
     * Signerar JWT.
     *
     * @param loginRequestDto användarens mejladress och lösenord
     * @return signerad token, giltighetstid i sekunder, användarens mejladress och roller
     */
    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.email(),
                        loginRequestDto.password()
                )
        );
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        List<String> roles = principal.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .toList();

        Instant now = Instant.now();
        Instant expiresAt = now.plus(jwtExpirationMinutes, ChronoUnit.MINUTES);

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(principal.getUsername())
                .claim("roles", roles);

        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId(jwtKeyId)
                .build();

        String accessToken = jwtEncoder.encode(
                JwtEncoderParameters.from(jwsHeader, claimsBuilder.build())
        ).getTokenValue();

        return new TokenResponseDto(
                accessToken, ChronoUnit.SECONDS.between(now, expiresAt), principal.getUsername(), roles
        );

    }


    /**
     * Vi publicerar Publika nyckeln som JWKS så att andra tjänster kan verifiera vår JWT-token
     *
     * @return JWKS med publika nyckeln
     */
    public Map<String, Object> publicJwkSet() {
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyID(jwtKeyId)
                .build();

        return new JWKSet(rsaKey).toJSONObject();
    }

    /**
     * Räknar återstående giltighetstid för token
     *
     * @param jwt tar emot token
     * @return återstående giltighetstid i sekunder, annars 0 ifall det saknas (kan aldrig vara negativt)
     */
    public long remainingSeconds(Jwt jwt) {
        Instant expiresAt = jwt.getExpiresAt();

        if (expiresAt == null) {
            return 0;
        }
        return Math.max(
                0,
                Duration.between(Instant.now(),
                        expiresAt).getSeconds()
        );
    }
}
