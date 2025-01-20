package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.web.vm.PasswordUpdateVM;
import org.anas.bidderx_rest.web.vm.ProfileUpdateVM;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        ProfileDTO profile = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody @Valid PasswordUpdateVM passwordUpdateVM,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.updateUserPassword(userDetails.getUsername(), passwordUpdateVM);
        return ResponseEntity.ok(new ApiResponse<>("Password updated successfully", null));
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(
            @RequestBody @Valid ProfileUpdateVM profileUpdateVM,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ProfileDTO updatedProfile = userService.updateUserProfile(userDetails.getUsername(), profileUpdateVM);
        return ResponseEntity.ok(new ApiResponse<>("Profile updated successfully", updatedProfile));
    }
}

