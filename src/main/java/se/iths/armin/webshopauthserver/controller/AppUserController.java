package se.iths.armin.webshopauthserver.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.webshopauthserver.dto.AppUserRequestDto;
import se.iths.armin.webshopauthserver.dto.AppUserResponseDto;
import se.iths.armin.webshopauthserver.dto.ChangeEmailDto;
import se.iths.armin.webshopauthserver.dto.ChangePasswordDto;
import se.iths.armin.webshopauthserver.service.AppUserService;

import java.util.List;

/**
 * Controller för USER funktioner (CRUD)
 */
@RestController
@RequestMapping("/appusers")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;


    /**
     * Hämtar alla användare
     *
     * @return alla användare samt status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<AppUserResponseDto>> getAllAppUsers() {
        List<AppUserResponseDto> users = appUserService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(users);

    }

    /**
     * Hämtar en specifik användare via ID
     * @param id ID:t på användare som ska returneras
     * @return användaren i form av response dto samt status 200 (OK)
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppUserResponseDto> findById(@PathVariable Long id) {
        AppUserResponseDto user = appUserService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * Registrera en ny användare
     *
     * @param appUserRequestDto mejl och lösenord som ska registreras
     *
     * @return användaren (utan lösenord) som skapats samt status 201 (CREATED)
     */
    @PostMapping("/register")
    public ResponseEntity<AppUserResponseDto> createAppUser(@RequestBody @Valid AppUserRequestDto appUserRequestDto) {
        AppUserResponseDto responseDto = appUserService.register(appUserRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * Byt lösenordet på en specifik användare
     *
     * @param changePasswordDto nya lösenordet
     * @param id vilken användare det gäller
     * @return status 200 (OK)
     */
    @PatchMapping("/update/password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto,
                                            @PathVariable Long id) {
        appUserService.changePassword(changePasswordDto, id);

        return ResponseEntity.ok().build();
    }

    /**
     * Byt mejl på en specifik användare
     *
     * @param changeEmailDto nya mejlen
     * @param id vilken användare det gäller
     * @return status 200 (OK)
     */
    @PatchMapping("/update/email/{id}")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid ChangeEmailDto changeEmailDto,
                                         @PathVariable Long id) {

        appUserService.changeEmail(changeEmailDto, id);

        return ResponseEntity.ok().build();
    }


    /**
     * Radera användare
     *
     * @param id ID:t för användare som ska raderas
     * @return status 204 (NO_CONTENT)
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        appUserService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
