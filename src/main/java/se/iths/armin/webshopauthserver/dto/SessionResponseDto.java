package se.iths.armin.webshopauthserver.dto;

import java.util.List;

public record SessionResponseDto(
        long expiresIn,
        String subject,
        List<String> roles
) {
}
