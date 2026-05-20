package se.iths.armin.webshopauthserver.dto;

import se.iths.armin.webshopauthserver.model.enums.UserRole;

public record AppUserResponseDto(
        Long userId,
        String email,
        UserRole role
) {
}
