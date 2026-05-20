package se.iths.armin.webshopauthserver.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(
        @NotBlank(message = "Password is required")
        String password
) {
}
