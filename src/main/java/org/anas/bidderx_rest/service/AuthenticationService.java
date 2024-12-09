package org.anas.bidderx_rest.service;

import jakarta.mail.MessagingException;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.enums.Role;
import org.anas.bidderx_rest.exceptions.CredentialsAlreadyExistException;
import org.anas.bidderx_rest.exceptions.UserNotFoundException;
import org.anas.bidderx_rest.exceptions.VerificationCodeException;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.anas.bidderx_rest.web.vm.LoginVM;
import org.anas.bidderx_rest.web.vm.RegisterVM;
import org.anas.bidderx_rest.web.vm.VerifyUserVM;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Service
public class AuthenticationService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            AppUserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public AppUser signup(RegisterVM input) {
//        if (userRepository.findByUsername(input.getUsername()).isPresent()) {
//            throw new CredentialsAlreadyExistException("Username already exists");
//        }
        if (userRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new CredentialsAlreadyExistException("Email already in use");
        }

//        if (userRepository.findByCin(input.getCin()).isPresent()) {
//            throw new CredentialsAlreadyExistException("CIN already registered");
//        }

        AppUser user = new AppUser(input.getProfileIdentifier(), input.getEmail(), passwordEncoder.encode(input.getPassword()), input.getFirstName(), input.getLastName(), input.getPhoneNumber());

        Set<Role> roles = new HashSet<>();
        roles.add(Role.BIDDER);  // Default role
        user.setRoles(roles);

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public AppUser authenticate(LoginVM input) {
        AppUser user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    public void verifyUser(VerifyUserVM input) {
        Optional<AppUser> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new VerificationCodeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new VerificationCodeException("Invalid verification code");
            }
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<AppUser> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    private void sendVerificationEmail(AppUser user) { //TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
