package se.iths.armin.webshopauthserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangeEmailDto(
        @NotBlank(message = "Email is required")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Invalid email format")
        String email
) {
}
