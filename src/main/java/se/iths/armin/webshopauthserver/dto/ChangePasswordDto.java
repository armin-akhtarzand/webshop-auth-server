package se.iths.armin.webshopauthserver.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(
        @NotBlank(message = "Lösenord krävs")
        String password
) {
}
