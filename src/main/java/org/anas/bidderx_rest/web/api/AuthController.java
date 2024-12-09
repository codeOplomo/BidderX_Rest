package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.AuthenticationService;
import org.anas.bidderx_rest.service.JwtService;
import org.anas.bidderx_rest.service.dto.AppUserTokenDTO;
import org.anas.bidderx_rest.service.dto.RegisterDTO;
import org.anas.bidderx_rest.web.vm.LoginVM;
import org.anas.bidderx_rest.web.vm.RegisterVM;
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



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginVM input) {
        try {
            AppUser user = authenticationService.authenticate(input);
            String jwtToken = jwtService.generateToken(user);

            // Use the AppUserTokenDTO to encapsulate the token
            AppUserTokenDTO response = new AppUserTokenDTO(jwtToken);

            return ResponseEntity.ok(response); // Return the DTO as the response
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }




    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyUserVM input) {
        try {
            authenticationService.verifyUser(input);
            return ResponseEntity.ok("User verified successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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


