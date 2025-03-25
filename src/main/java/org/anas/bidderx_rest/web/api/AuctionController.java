package org.anas.bidderx_rest.web.api;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.anas.bidderx_rest.domain.enums.AuctionType;
import org.anas.bidderx_rest.exceptions.InvalidProductRequestException;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.anas.bidderx_rest.service.dto.AuctionsStateDTO;
import org.anas.bidderx_rest.service.dto.OwnerRankingDTO;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.anas.bidderx_rest.web.vm.RejectAuctionVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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


    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<Page<OwnerRankingDTO>>> getOwnerRanking(
            @RequestParam String duration,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        LocalDateTime startDate = calculateStartDate(duration);

        Pageable pageable = PageRequest.of(page, size, Sort.by("totalRevenue").descending());


        Page<OwnerRankingDTO> rankings = auctionService.getOwnerRanking(startDate, pageable);

        return ResponseEntity.ok(new ApiResponse<>("Ranking retrieved", rankings));
    }

    private LocalDateTime calculateStartDate(String duration) {
        LocalDateTime now = LocalDateTime.now();
        return switch (duration.toLowerCase()) {
            case "1d" -> now.minusDays(1);
            case "7d" -> now.minusDays(7);
            case "15d" -> now.minusDays(15);
            case "1m" -> now.minusMonths(1);
            default -> throw new IllegalArgumentException("Invalid duration. Use: 1d,7d,15d,1m");
        };
    }

    @GetMapping("/victories")
    public ResponseEntity<ApiResponse<Page<AuctionDTO>>> getUserWonAuctions(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AuctionDTO> wonAuctions = auctionService.findWonAuctionsByEmail(email, pageable);
        return ResponseEntity.ok(new ApiResponse<>("Won auctions retrieved successfully", wonAuctions));
    }

    @PreAuthorize("!principal.banned and hasRole('OWNER')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AuctionDTO>> createAuction(
            @RequestPart("vm") @Valid CreateAuctionVM request,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
            @AuthenticationPrincipal AppUser authenticatedUser
    ) {
        System.out.println("mainImage = " + mainImage);
        System.out.println("additionalImages = " + additionalImages);
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

    @PreAuthorize("!principal.banned")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AuctionDTO>>> searchAuctions(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int limit) {

        List<AuctionDTO> results = auctionService.searchAuctions(q, limit);
        return ResponseEntity.ok(new ApiResponse<>("Search results", results));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuctionDTO>>> getAuctionsByFilter(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) @Min(0) Double minPrice,
            @RequestParam(required = false) @Min(0) Double maxPrice,
            @RequestParam(required = false) AuctionStatus status,
            @RequestParam(required = false) AuctionType type,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer startDate,
            @RequestParam(required = false) Integer endDate,
            @RequestParam(defaultValue = "DESC") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), "startTime");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<AuctionDTO> auctions = auctionService.getAuctionsByFilter(categoryId, minPrice, maxPrice, status, type, q, startDate, endDate, pageable);
        return ResponseEntity.ok(new ApiResponse<>("Auctions retrieved successfully", auctions));
    }


    @GetMapping("/live")
    public ResponseEntity<ApiResponse<Page<AuctionDTO>>> getLiveAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @AuthenticationPrincipal AppUser user) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuctionDTO> auctions = auctionService.getLiveAuctions(pageable, user);
        return ResponseEntity.ok(new ApiResponse<>("Live auctions retrieved successfully", auctions));
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


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{auctionId}/approve")
    public ResponseEntity<ApiResponse<AuctionDTO>> approveAuction(
            @PathVariable UUID auctionId,
            @AuthenticationPrincipal AppUser adminUser) {
        AuctionDTO auctionDTO = auctionService.approveAuction(auctionId, adminUser);
        return ResponseEntity.ok(new ApiResponse<>("Auction approved successfully", auctionDTO));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{auctionId}/reject")
    public ResponseEntity<ApiResponse<AuctionDTO>> rejectAuction(
            @PathVariable UUID auctionId,
            @Valid @RequestBody RejectAuctionVM request,
            @AuthenticationPrincipal AppUser adminUser) {
        AuctionDTO auctionDTO = auctionService.rejectAuction(auctionId, request.getReason(), adminUser);
        return ResponseEntity.ok(new ApiResponse<>("Auction rejected successfully", auctionDTO));
    }


}
