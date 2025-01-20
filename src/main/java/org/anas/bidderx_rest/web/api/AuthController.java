package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.AuthenticationService;
import org.anas.bidderx_rest.service.JwtService;
import org.anas.bidderx_rest.service.dto.*;
import org.anas.bidderx_rest.web.vm.LoginVM;
import org.anas.bidderx_rest.web.vm.RegisterVM;
import org.anas.bidderx_rest.web.vm.TokenRefreshVM;
import org.anas.bidderx_rest.web.vm.VerifyUserVM;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody @Valid TokenRefreshVM refreshRequest) {
        RefreshTokenResponseDTO tokenResponse = authenticationService.refreshTokens(refreshRequest.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid LoginVM input) {
        AppUser user = authenticationService.authenticate(input);
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return ResponseEntity.ok(new TokenResponseDTO(accessToken, refreshToken));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterDTO> register(@RequestBody @Valid RegisterVM input) {
        AppUser user = authenticationService.signup(input);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterDTO("Registration successful!", user.getEmail()));
    }

    @PostMapping("/verify")
    public ResponseEntity<MessageDTO> verify(@RequestBody @Valid VerifyUserVM input) {
        authenticationService.verifyUser(input);
        return ResponseEntity.ok(new MessageDTO("User verified successfully."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageDTO> resendVerification(@RequestParam @Email String email) {
        authenticationService.resendVerificationCode(email);
        return ResponseEntity.ok(new MessageDTO("Verification code resent successfully."));
    }
}


