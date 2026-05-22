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
    public ResponseEntity<AppUserResponseDto> findById(@PathVariable Long id) {
        AppUserResponseDto user = appUserService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/register")
    public ResponseEntity<AppUserResponseDto> createAppUser(@RequestBody @Valid AppUserRequestDto appUserRequestDto) {
        AppUserResponseDto responseDto = appUserService.register(appUserRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PatchMapping("/update/password/{id}")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordDto changePasswordDto,
                                            @PathVariable Long id) {
        appUserService.changePassword(changePasswordDto, id);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update/email/{id}")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid ChangeEmailDto changeEmailDto,
                                         @PathVariable Long id) {

        appUserService.changeEmail(changeEmailDto, id);

        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        appUserService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
