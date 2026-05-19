package se.iths.armin.webshopauthserver.dto;

import java.util.List;

public record TokenResponseDto(
        String accessToken,
        long expiresIn,
        String subject,
        List<String> roles
) {
}
