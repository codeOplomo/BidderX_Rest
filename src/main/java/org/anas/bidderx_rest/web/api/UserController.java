package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.service.dto.ResponseMessageDTO;
import org.anas.bidderx_rest.web.vm.PasswordUpdateVM;
import org.anas.bidderx_rest.web.vm.ProfileUpdateVM;
import org.anas.bidderx_rest.web.vm.ImageUploadVM;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getProfile(Principal principal) {
        String email = principal.getName(); // Extract the logged-in user's email
        ProfileDTO profile = userService.getUserProfile(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody @Valid ProfileUpdateVM profileUpdateVM,
            Authentication authentication
    ) {
        ProfileDTO updatedUser = userService.updateUserProfile(authentication, profileUpdateVM);
        return ResponseEntity.ok(new ResponseMessageDTO("Profile updated successfully", updatedUser));
    }

    @PostMapping("/upload-image")
    public ResponseEntity<ProfileDTO> uploadProfileImage(
            Authentication authentication,
            @Valid @ModelAttribute ImageUploadVM imageUploadVM
    ) {
        ProfileDTO updatedProfile = userService.uploadUserProfileImage(authentication, imageUploadVM.getImage());
        return ResponseEntity.ok(updatedProfile);
    }



    @PutMapping("/change-password")
    public ResponseEntity<?> updatePassword(
            @RequestBody @Valid PasswordUpdateVM passwordUpdateVM,
            Authentication authentication
    ) {
        userService.updateUserPassword(authentication, passwordUpdateVM);
        return ResponseEntity.ok(new ResponseMessageDTO("Password updated successfully"));
    }
}

