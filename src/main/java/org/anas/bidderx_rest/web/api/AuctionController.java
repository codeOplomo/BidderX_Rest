package org.anas.bidderx_rest.web.api;


import jakarta.validation.Valid;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.exceptions.InvalidProductRequestException;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.anas.bidderx_rest.service.dto.AuctionsStateDTO;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.anas.bidderx_rest.web.vm.RejectAuctionVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<AuctionDTO>> getAuctionById(@PathVariable UUID auctionId, @AuthenticationPrincipal AppUser user) {
        AuctionDTO auctionDTO = auctionService.getAuctionById(auctionId, user);
        return ResponseEntity.ok(new ApiResponse<>("Auction retrieved successfully", auctionDTO));
    }


    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AuctionsStateDTO>> getAuctionStats() {
        AuctionsStateDTO stats = auctionService.getAuctionsStats();
        return ResponseEntity.ok(new ApiResponse<>("Stats retrieved successfully", stats));
    }

//

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<AuctionDTO>>> getPendingAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuctionDTO> pendingAuctions = auctionService.findPendingAuctions(pageable);
        return ResponseEntity.ok(new ApiResponse<>("Pending auctions retrieved successfully", pendingAuctions));
    }


    @PutMapping("/{auctionId}/approve")
    public ResponseEntity<ApiResponse<AuctionDTO>> approveAuction(
            @PathVariable UUID auctionId,
            @AuthenticationPrincipal AppUser adminUser) {
        AuctionDTO auctionDTO = auctionService.approveAuction(auctionId, adminUser);
        return ResponseEntity.ok(new ApiResponse<>("Auction approved successfully", auctionDTO));
    }


    @PutMapping("/{auctionId}/reject")
    public ResponseEntity<ApiResponse<AuctionDTO>> rejectAuction(
            @PathVariable UUID auctionId,
            @Valid @RequestBody RejectAuctionVM request,
            @AuthenticationPrincipal AppUser adminUser) {
        AuctionDTO auctionDTO = auctionService.rejectAuction(auctionId, request.getReason(), adminUser);
        return ResponseEntity.ok(new ApiResponse<>("Auction rejected successfully", auctionDTO));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AuctionDTO>> createAuction(
            @RequestPart("vm") @Valid CreateAuctionVM request,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
            @AuthenticationPrincipal AppUser authenticatedUser
    ) {
        AuctionDTO auctionDTO = auctionService.createAuction(request, mainImage, additionalImages, authenticatedUser);
        return ResponseEntity.ok(new ApiResponse<>("Auction created successfully", auctionDTO));
    }


    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Page<AuctionDTO>>> getUserAuctions(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuctionDTO> userAuctions = auctionService.findAuctionsByEmail(email, pageable);
        return ResponseEntity.ok(new ApiResponse<>("Auctions retrieved successfully", userAuctions));
    }


    @GetMapping("/liked")
    public ResponseEntity<ApiResponse<Page<AuctionDTO>>> getUserLikedAuctions(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuctionDTO> likedAuctions = auctionService.findLikedAuctionsByEmail(email, pageable);
        return ResponseEntity.ok(new ApiResponse<>("Liked auctions retrieved successfully", likedAuctions));
    }

}
//@GetMapping("/victories")
//    public ResponseEntity<ApiResponse<Page<AuctionDTO>>> getUserWonAuctions(
//            @RequestParam String email,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "12") int size
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<AuctionDTO> wonAuctions = auctionService.findWonAuctionsByEmail(email, pageable);
//        return ResponseEntity.ok(new ApiResponse<>("Won auctions retrieved successfully", wonAuctions));
//    }