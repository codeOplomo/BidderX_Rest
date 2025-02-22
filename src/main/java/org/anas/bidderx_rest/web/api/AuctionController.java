package org.anas.bidderx_rest.web.api;


import jakarta.validation.Valid;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.exceptions.InvalidProductRequestException;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {
    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
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
}
