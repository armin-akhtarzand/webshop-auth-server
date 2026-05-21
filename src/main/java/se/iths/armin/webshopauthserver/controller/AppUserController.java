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

@RestController
@RequestMapping("/appusers")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;


    @GetMapping
    public ResponseEntity<List<AppUserResponseDto>> getAllAppUsers() {
        List<AppUserResponseDto> users = appUserService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(users);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        AppUserResponseDto user = appUserService.findById(id);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createAppUser(@RequestBody @Valid AppUserRequestDto appUserRequestDto) {
        AppUserResponseDto responseDto = appUserService.register(appUserRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/update/password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto,
                                            @PathVariable Long id) {

        AppUserResponseDto existingUser = appUserService.findById(id);

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        AppUserResponseDto updatedUser = appUserService.changePassword(changePasswordDto, id);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @PutMapping("/update/email/{id}")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid ChangeEmailDto changeEmailDto,
                                         @PathVariable Long id) {

        AppUserResponseDto existingUser = appUserService.findById(id);

        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        AppUserResponseDto updatedUser = appUserService.changeEmail(changeEmailDto, id);

        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        AppUserResponseDto existingUser = appUserService.findById(id);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        appUserService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
