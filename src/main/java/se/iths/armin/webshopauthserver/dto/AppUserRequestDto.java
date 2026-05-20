package se.iths.armin.webshopauthserver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AppUserRequestDto(
        //Pattern kräver 1.text | 2.@ | 3.domän(text) | 4.punkt | 5.toppdomän (minst 2 bokstäver)
        @NotBlank(message = "Email is required")
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "Invalid email format")
        String email,
        @NotBlank(message = "Password is required")
        String password

) {
}
