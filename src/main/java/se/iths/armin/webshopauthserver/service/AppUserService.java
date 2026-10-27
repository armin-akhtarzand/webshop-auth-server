package se.iths.armin.webshopauthserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.iths.armin.webshopauthserver.dto.AppUserRequestDto;
import se.iths.armin.webshopauthserver.dto.AppUserResponseDto;
import se.iths.armin.webshopauthserver.dto.ChangeEmailDto;
import se.iths.armin.webshopauthserver.dto.ChangePasswordDto;
import se.iths.armin.webshopauthserver.exception.BadRequestException;
import se.iths.armin.webshopauthserver.exception.DuplicateFoundException;
import se.iths.armin.webshopauthserver.exception.ForbiddenRequestException;
import se.iths.armin.webshopauthserver.exception.UserNotFoundException;
import se.iths.armin.webshopauthserver.mapper.AppUserMapper;
import se.iths.armin.webshopauthserver.model.AppUser;
import se.iths.armin.webshopauthserver.model.enums.UserRole;
import se.iths.armin.webshopauthserver.repository.AppUserRepository;

import java.util.List;


/**
 * Service för affärslogik när det gäller CRUD metoder
 */
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;


    /**
     * Hämtar och lägger till alla användare i en lista
     *
     * @return mappar om listan till en DTO lista och returnerar
     */
    public List<AppUserResponseDto> findAll() {
        List<AppUser> appUsers = appUserRepository.findAll();

        return appUsers.stream().map(appUserMapper::toDto).toList();

    }

    /**
     * Hämtar användaren med ID
     * returnerar informationen som DTo
     *
     * @param id tar emot relevant användares ID
     * @return mappar användaren information till response dto och returnerar
     * @throws UserNotFoundException ifall användaren inte finns
     */
    public AppUserResponseDto findById(Long id) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        return appUserMapper.toDto(appUser);
    }

    /**
     * Mappar request(registering uppgifterna) till en entitet.
     * Kontrollerar att mejladressen inte redan används,
     * hashar lösenordet,
     * och sparar den nya användaren i databasen
     *
     * @param appUserRequestDto vi tar emot de inloggningsuppgifter användaren vill registrera
     * @return vi returnerar den nya användarens information i form av DTO
     * @throws DuplicateFoundException om mejladressen redan används
     */
    @Transactional
    public AppUserResponseDto register(AppUserRequestDto appUserRequestDto) {
        AppUser appUser = appUserMapper.toEntity(appUserRequestDto);

        if (appUserRepository.findByEmail(appUser.getEmail()).isPresent()) {
            throw new DuplicateFoundException("Email already exists");
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser = appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    /**
     * Verifierar att användaren finns,
     * validerar användaren (antingen admin eller den som ska byta mejl på sig själv).
     * ser så att den nya mejlen inte är desamma som de befintliga.
     * sparar ändringen.
     *
     * @param changeEmailDto tar emot användarens nya mejl
     * @param id             den användare ändringen ska ske på.
     * @throws BadRequestException     ifall nya mejlen är desamma som det befintliga.
     * @throws DuplicateFoundException ifall mejlen redan används
     * @throws UserNotFoundException   ifall användaren inte hittas i databasen
     */
    @Transactional
    public void changeEmail(ChangeEmailDto changeEmailDto, Long id) {

        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateSelfOrAdmin(existingUser.getUserId());

        if (existingUser.getEmail().equals(changeEmailDto.email())) {
            throw new BadRequestException("Invalid email change");
        }
        if (appUserRepository.findByEmail(changeEmailDto.email()).isPresent()) {
            throw new DuplicateFoundException("Email already used");
        }

        existingUser.setEmail(changeEmailDto.email());


        AppUser saved = appUserRepository.save(existingUser);

    }

    /**
     * Verifierar att användaren finns,
     * validerar användaren (antingen admin eller den som ska byta lösenord på sig själv).
     * ser så att de nya lösenordet inte är desamma som de befintliga.
     * hashar lösenordet,
     * sparar ändringen.
     *
     * @param changePasswordDto tar emot användarens nya lösenord
     * @param id                den användare ändringen ska ske på.
     * @throws BadRequestException   ifall nya lösenordet är desamma som det befintliga.
     * @throws UserNotFoundException ifall användaren inte hittas i databasen
     */
    @Transactional
    public void changePassword(ChangePasswordDto changePasswordDto, Long id) {
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateSelfOrAdmin(existingUser.getUserId());

        if (passwordEncoder.matches(changePasswordDto.password(), existingUser.getPassword())) {
            throw new BadRequestException("Invalid password change");
        }

        existingUser.setPassword(passwordEncoder.encode(changePasswordDto.password()));
        AppUser saved = appUserRepository.save(existingUser);

    }

    /**
     * Kontrollerar så att användaren är admin
     * eller har samma ID som den hämtade användaren (inloggad)
     *
     * @param id ID för den användare som det gäller
     * @throws UserNotFoundException     om den inloggade användaren saknas i databasen
     * @throws ForbiddenRequestException om användaren varken är admin eller har det relevanta ID:t
     */
    public void validateSelfOrAdmin(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        String email = authentication.getName();


        AppUser currentUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        boolean isSelf = currentUser.getUserId().equals(id);
        boolean isAdmin = currentUser.getRole().equals(UserRole.ROLE_ADMIN);


        if (!isSelf && !isAdmin) {
            throw new ForbiddenRequestException("You are not allowed to perform this action");
        }
    }

    /**
     * Verfierar att användaren finns
     * validerar behörighet (antingen admin eller användaren själv)
     * raderar användaren
     *
     * @param id ID:t för användaren som ska raderas
     * @throws UserNotFoundException ifall användaren som ska raderas inte hittas i databasen.
     */
    @Transactional
    public void delete(Long id) {
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateSelfOrAdmin(existingUser.getUserId());

        appUserRepository.delete(existingUser);
    }


}
