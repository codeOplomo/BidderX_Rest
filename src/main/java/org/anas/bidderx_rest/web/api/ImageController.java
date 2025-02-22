package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.FeaturedImage;
import org.anas.bidderx_rest.service.CollectionService;
import org.anas.bidderx_rest.service.FeaturedImageService;
import org.anas.bidderx_rest.service.FileStorageService;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final CollectionService collectionService;
    private final FeaturedImageService featuredImageService;

    public ImageController(FileStorageService fileStorageService, UserService userService, CollectionService collectionService, FeaturedImageService featuredImageService) {
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.collectionService = collectionService;
        this.featuredImageService = featuredImageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @RequestPart("image") @Valid @NotNull MultipartFile image,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "collectionId", required = false) UUID collectionId,
            @RequestParam(value = "productId", required = false) UUID productId,
            @AuthenticationPrincipal AppUser authenticatedUser
    ) {
        System.out.println("Type: " + type); // Debugging output
        System.out.println("Collection ID: " + collectionId); // Debugging output

        // Determine the target folder
        String targetFolder = switch (type) {
            case "cover" -> "cover-images";
            case "profile" -> "profile-images";
            case "collection-cover" -> "collection-cover-images";
            case "product-featured" -> "product-images/featured"; // Subfolder for featured images
            default -> "general";
        };

        // Store the file in the correct folder and get its URL
        String imageUrl = fileStorageService.storeFile(image, targetFolder);

        // Update the user's profile or cover image in the database
        switch (type) {
            case "cover" -> userService.uploadCoverImage(authenticatedUser, imageUrl);
            case "profile" -> userService.uploadProfileImage(authenticatedUser, imageUrl);
            case "collection-cover" -> collectionService.uploadShowcaseImage(collectionId, imageUrl);
            case "product-featured" -> {
                if (productId == null) {
                    throw new IllegalArgumentException("Product ID is required for featured images");
                }
                featuredImageService.saveFeaturedImage(productId, "product", imageUrl);
            }
            // 'product-main' doesn't update here; handled during product creation
        }

        return ResponseEntity.ok(new ApiResponse<>("Image uploaded successfully", imageUrl));
    }

}


