package se.iths.armin.webshopauthserver.mapper;

import org.springframework.stereotype.Component;
import se.iths.armin.webshopauthserver.dto.AppUserRequestDto;
import se.iths.armin.webshopauthserver.dto.AppUserResponseDto;
import se.iths.armin.webshopauthserver.model.AppUser;

/**
 * Mappar till och från DTO
 * response innehåller aldrig Password
 */
@Component
public class AppUserMapper {


    /**
     * Mappar från requestDto till entitet
     *
     * @param requestDto request med användarinformation tas emot
     * @return appUser entitet
     */
    public AppUser toEntity(AppUserRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        AppUser appUser = new AppUser();
        appUser.setEmail(requestDto.email());
        appUser.setPassword(requestDto.password());

        return appUser;
    }

    /**
     * Mappar från entitet till responseDto
     *
     * @param appUser objekt tas emot
     * @return response dto med användarinformation (inte lösenord)
     */
    public AppUserResponseDto toDto(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        return new AppUserResponseDto(appUser.getUserId(), appUser.getEmail(), appUser.getRole());
    }


}
