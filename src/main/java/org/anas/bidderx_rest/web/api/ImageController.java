package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.anas.bidderx_rest.service.FileStorageService;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final FileStorageService fileStorageService;
    private final UserService userService;

    public ImageController(FileStorageService fileStorageService, UserService userService) {
        this.fileStorageService = fileStorageService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestPart("image") @Valid @NotNull MultipartFile image,
            @RequestParam(value = "type", required = false) String type,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        System.out.println("Type: " + type); // Debugging output

        // Determine the target folder
        String targetFolder = "general";
        if ("cover".equals(type)) {
            targetFolder = "cover-images";
        } else if ("profile".equals(type)) {
            targetFolder = "profile-images";
        } else if ("collection-cover".equals(type)) {
            targetFolder = "collection-cover-images";
        }

        // Store the file in the correct folder and get its URL
        String imageUrl = fileStorageService.storeFile(image, targetFolder);

        // Update the user's profile or cover image in the database
        if ("cover".equals(type)) {
            userService.uploadCoverImage(userDetails.getUsername(), imageUrl);
        } else if ("profile".equals(type)) {
            userService.uploadProfileImage(userDetails.getUsername(), imageUrl);
        }

        return ResponseEntity.ok(new ApiResponse<>("Image uploaded successfully", imageUrl));
    }

}


