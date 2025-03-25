package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.enums.OwnerRequestStatus;
import org.anas.bidderx_rest.domain.enums.Role;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.OwnerRankingDTO;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.web.vm.PasswordUpdateVM;
import org.anas.bidderx_rest.web.vm.ProfileUpdateVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<OwnerRankingDTO>>> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<OwnerRankingDTO> results = userService.searchUsers(q, pageable);
        return ResponseEntity.ok(new ApiResponse<>("Users found", results));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{email}/ban")
    public ResponseEntity<ApiResponse<Void>> banUser(@PathVariable String email) {
        userService.banUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("User banned successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{email}/unban")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable String email) {
        userService.unbanUserByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("User unbanned successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileDTO>>> getAllUsers() {
        List<ProfileDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(new ApiResponse<>("Users retrieved", users));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{email}/approve-owner")
    public ResponseEntity<ApiResponse<Void>> approveOwnerRequest(@PathVariable String email) {
        userService.approveOwnerRequestByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("Owner request approved", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{email}/reject-owner")
    public ResponseEntity<ApiResponse<Void>> rejectOwnerRequest(@PathVariable String email) {
        userService.rejectOwnerRequestByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("Owner request rejected", null));
    }

    @PreAuthorize("!principal.banned and hasRole('BIDDER')")
    @PostMapping("/become-owner")
    public ResponseEntity<ApiResponse<Void>> requestOwnerUpgrade(@AuthenticationPrincipal AppUser currentUser) {
        // Retrieve current user from security context or principal
        String email = currentUser.getUsername();
        AppUser user = userService.findUserByEmail(email);

        // Set the owner request status to PENDING
        user.setOwnerRequestStatus(OwnerRequestStatus.PENDING);
        userService.save(user);

        // Optionally trigger an email/notification to admin here

        return ResponseEntity.ok(new ApiResponse<>("Your owner upgrade request has been submitted.", null));
    }

    @GetMapping("/{email}/profile")
    public ResponseEntity<ApiResponse<ProfileDTO>> getUserProfileByEmail(
            @PathVariable String email) {
        ProfileDTO profile = userService.getUserProfileByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("Profile retrieved", profile));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        ProfileDTO profile = userService.getUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>("Profile retreived", profile));
    }

    @PreAuthorize("!principal.banned")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @RequestBody @Valid PasswordUpdateVM passwordUpdateVM,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        userService.updateUserPassword(userDetails.getUsername(), passwordUpdateVM);
        return ResponseEntity.ok(new ApiResponse<>("Password updated successfully", null));
    }

    @PreAuthorize("!principal.banned")
    @PutMapping("/edit-profile")
    public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(
            @RequestBody @Valid ProfileUpdateVM profileUpdateVM,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ProfileDTO updatedProfile = userService.updateUserProfile(userDetails.getUsername(), profileUpdateVM);
        return ResponseEntity.ok(new ApiResponse<>("Profile updated successfully", updatedProfile));
    }
}

