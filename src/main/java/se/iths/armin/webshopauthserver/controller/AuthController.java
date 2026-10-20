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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(
            @RequestBody @Valid LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @GetMapping("/jwks")
    public ResponseEntity<Map<String, Object>> publicJwks() {
        return ResponseEntity.ok(authService.publicJwkSet());
    }

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
