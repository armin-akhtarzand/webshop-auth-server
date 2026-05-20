package se.iths.armin.webshopauthserver.mapper;

import org.springframework.stereotype.Component;
import se.iths.armin.webshopauthserver.dto.AppUserRequestDto;
import se.iths.armin.webshopauthserver.dto.AppUserResponseDto;
import se.iths.armin.webshopauthserver.dto.ChangeEmailDto;
import se.iths.armin.webshopauthserver.dto.ChangePasswordDto;
import se.iths.armin.webshopauthserver.model.AppUser;

@Component
public class AppUserMapper {


    public AppUser toEntity(AppUserRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        AppUser appUser = new AppUser();
        appUser.setEmail(requestDto.email());
        appUser.setPassword(requestDto.password());

        return appUser;
    }

    public AppUserResponseDto toDto(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        return new AppUserResponseDto(appUser.getUserId(), appUser.getEmail(), appUser.getRole());
    }

    public void changePassword(ChangePasswordDto passwordDto, AppUser appUser) {
        if (passwordDto == null) {
            return;
        }
        appUser.setPassword(passwordDto.password());
    }

    public void changeEmail(ChangeEmailDto emailDto, AppUser appUser) {
        if (emailDto == null) {
            return;
        }
        appUser.setEmail(emailDto.email());
    }

}
