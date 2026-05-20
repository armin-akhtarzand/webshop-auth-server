package se.iths.armin.webshopauthserver.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        String password
) {
}
