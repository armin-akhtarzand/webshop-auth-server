package se.iths.armin.webshopauthserver.mapper;

import org.springframework.stereotype.Component;
import se.iths.armin.webshopauthserver.dto.AppUserRequestDto;
import se.iths.armin.webshopauthserver.dto.AppUserResponseDto;
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


}
