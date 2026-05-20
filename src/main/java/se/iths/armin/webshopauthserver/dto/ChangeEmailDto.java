package se.iths.armin.webshopauthserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangeEmailDto(
        @NotBlank(message = "E-postadress krävs")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Ogiltigt E-post format")
        String email
) {
}
