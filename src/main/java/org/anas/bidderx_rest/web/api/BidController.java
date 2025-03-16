package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.BidService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.BidDTO;
import org.anas.bidderx_rest.web.vm.BidVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/bids")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }


    @GetMapping("/user/{auctionId}")
    public ResponseEntity<ApiResponse<Page<BidDTO>>> getUserBidsForAuction(
            @PathVariable UUID auctionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bidTime,desc") String[] sort,
            @AuthenticationPrincipal AppUser userDetails) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortOrder(sort)));
        Page<BidDTO> bids = bidService.getUserBidsForAuction(
                auctionId,
                userDetails,
                pageable
        );

        return ResponseEntity.ok(
                new ApiResponse<>("User bids fetched successfully", bids)
        );
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<ApiResponse<Page<BidDTO>>> getBidsForAuction(
            @PathVariable UUID auctionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "bidTime,desc") String[] sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortOrder(sort)));
        Page<BidDTO> bids = bidService.getBidsForAuction(auctionId, pageable);
        return ResponseEntity.ok(
                new ApiResponse<>("Bids fetched successfully", bids)
        );
    }

    private Sort.Order parseSortOrder(String[] sort) {
        String property = sort[0];
        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return new Sort.Order(direction, property);
    }

    @PreAuthorize("!principal.banned")
    @PostMapping("/place-bid")
    public ResponseEntity<ApiResponse<BidDTO>> placeBid(
            @Valid @RequestBody BidVM bidVM,
            @AuthenticationPrincipal AppUser currentUser) {

        BidDTO response = bidService.placeBid(bidVM, currentUser);

        return ResponseEntity.ok(
                new ApiResponse<>("Bid placed successfully", response)
        );
    }
}
