package com.example.loginregistrationconfirmemail.registration;

import com.example.loginregistrationconfirmemail.appuser.AppUser;
import com.example.loginregistrationconfirmemail.appuser.AppUserRole;
import com.example.loginregistrationconfirmemail.appuser.AppUserService;
import com.example.loginregistrationconfirmemail.registration.token.ConfirmationToken;
import com.example.loginregistrationconfirmemail.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;

    public String register(RegistrationRequest request) {

        boolean isValidEmail = emailValidator.test(request.getEmail());
        if(!isValidEmail){
            throw new IllegalStateException("Email not valid");
        }
        return appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );
    }

    @Transactional
    public String confirmToken (String token) {

        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(()-> new IllegalStateException("Token not found"));

        if(confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if(expiredAt.isBefore(LocalDateTime.now())){
            throw new IllegalIdentifierException("Token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enabledAppUser(confirmationToken.getAppUser().getEmail());

        return "Confirmed";

    }
}
