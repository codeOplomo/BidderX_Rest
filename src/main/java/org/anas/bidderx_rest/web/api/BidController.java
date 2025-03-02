package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.BidService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.BidDTO;
import org.anas.bidderx_rest.web.vm.BidVM;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api/bids")
public class BidController {
//    private final BidService bidService;
//
//    public BidController(BidService bidService) {
//        this.bidService = bidService;
//    }
//
//    public ResponseEntity<ApiResponse<BidDTO>> placeBid(
//            @PathVariable UUID auctionId,
//            @Valid @RequestBody BidVM bidVM,
//            @AuthenticationPrincipal AppUser currentUser) {
//
//        BidDTO response = bidService.placeBid(auctionId, bidVM, currentUser);
//
//        return ResponseEntity.ok(
//                new ApiResponse<>("Bid placed successfully", response)
//        );
//    }
}
