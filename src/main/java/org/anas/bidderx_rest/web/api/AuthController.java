package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.AuthenticationService;
import org.anas.bidderx_rest.service.JwtService;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.*;
import org.anas.bidderx_rest.web.vm.LoginVM;
import org.anas.bidderx_rest.web.vm.RegisterVM;
import org.anas.bidderx_rest.web.vm.TokenRefreshVM;
import org.anas.bidderx_rest.web.vm.VerifyUserVM;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationService authenticationService, JwtService jwtService, UserService userService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody TokenRefreshVM refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        // Call the service method to refresh tokens
        RefreshTokenResponseDTO tokenResponse = authenticationService.refreshTokens(refreshToken);

        if (tokenResponse.getNewAccessToken() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(tokenResponse);
        }

        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginVM input) {
        try {
            AppUser user = authenticationService.authenticate(input);
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            TokenResponseDTO response = new TokenResponseDTO(accessToken, refreshToken);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterVM input) {
        try {
            AppUser user = authenticationService.signup(input);
            // Create a ResponseDTO object
            RegisterDTO response = new RegisterDTO("Registration successful!", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RegisterDTO(e.getMessage(), null));
        }
    }




    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyUserVM input) {
        try {
            authenticationService.verifyUser(input);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User verified successfully.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code resent successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}


