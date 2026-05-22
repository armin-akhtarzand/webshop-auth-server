package se.iths.armin.webshopauthserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.iths.armin.webshopauthserver.dto.AppUserRequestDto;
import se.iths.armin.webshopauthserver.dto.AppUserResponseDto;
import se.iths.armin.webshopauthserver.dto.ChangeEmailDto;
import se.iths.armin.webshopauthserver.dto.ChangePasswordDto;
import se.iths.armin.webshopauthserver.exception.DuplicateFoundException;
import se.iths.armin.webshopauthserver.exception.UnauthorizedException;
import se.iths.armin.webshopauthserver.exception.UserNotFoundException;
import se.iths.armin.webshopauthserver.mapper.AppUserMapper;
import se.iths.armin.webshopauthserver.model.AppUser;
import se.iths.armin.webshopauthserver.model.enums.UserRole;
import se.iths.armin.webshopauthserver.repository.AppUserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;


    public List<AppUserResponseDto> findAll() {
        List<AppUser> appUsers = appUserRepository.findAll();

        return appUsers.stream().map(appUserMapper::toDto).toList();

    }

    public AppUserResponseDto findById(Long id) {
        AppUser appUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        return appUserMapper.toDto(appUser);
    }

    public AppUserResponseDto register(AppUserRequestDto appUserRequestDto) {
        AppUser appUser = appUserMapper.toEntity(appUserRequestDto);

        if (appUserRepository.findByEmail(appUser.getEmail()).isPresent()) {
            throw new DuplicateFoundException("Email already exists");
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser = appUserRepository.save(appUser);
        return appUserMapper.toDto(appUser);
    }

    public AppUserResponseDto changeEmail(ChangeEmailDto changeEmailDto, Long id) {

        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateSelfOrAdmin(existingUser.getUserId());

        if (existingUser.getEmail().equals(changeEmailDto.email())) {
            throw new DuplicateFoundException("Invalid email change");
        }
        if (appUserRepository.findByEmail(changeEmailDto.email()).isPresent()) {
            throw new DuplicateFoundException("Email already used");
        }

        existingUser.setEmail(changeEmailDto.email());


        AppUser saved = appUserRepository.save(existingUser);

        return appUserMapper.toDto(saved);
    }

    public AppUserResponseDto changePassword(ChangePasswordDto changePasswordDto, Long id) {
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateSelfOrAdmin(existingUser.getUserId());

        if (passwordEncoder.matches(changePasswordDto.password(), existingUser.getPassword())) {
            throw new DuplicateFoundException("Invalid password change");
        }

        existingUser.setPassword(passwordEncoder.encode(changePasswordDto.password()));
        AppUser saved = appUserRepository.save(existingUser);
        return appUserMapper.toDto(saved);


    }

    //Kollar behörighet (admin eller själv) vid t.ex byte av email, lösenord eller ta bort användare
    public void validateSelfOrAdmin(Long id) {
        //Hämtar auktoriserad användaren via SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Hämtar användares "username" -> email
        String email = authentication.getName();

        //Hämtar användaren från databasen
        AppUser currentUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        //Kollar ifall användaren antingen är sig själv
        // (alltså den som är inloggad och ska göra ändring på sig själv)
        //Eller ifall användaren är Admin
        boolean isSelf = currentUser.getUserId().equals(id);
        boolean isAdmin = currentUser.getRole().equals(UserRole.ROLE_ADMIN);

        //IFALL de varken är "sig själv" eller ADMIN så har de inte behörighet att utföra handling
        if (!isSelf && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to perform this action");
        }
    }

    public void delete(Long id) {
        AppUser existingUser = appUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        validateSelfOrAdmin(existingUser.getUserId());

        appUserRepository.delete(existingUser);
    }


}
