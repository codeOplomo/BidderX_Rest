package org.anas.bidderx_rest.web.api;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.AuctionReactionService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.LikeStatusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/auction-reactions")
public class AuctionReactionController {
    private final AuctionReactionService auctionReactionService;

    public AuctionReactionController(AuctionReactionService auctionReactionService) {
        this.auctionReactionService = auctionReactionService;
    }

    @PostMapping("/{auctionId}/like")
    public ResponseEntity<ApiResponse<LikeStatusDTO>> toggleLike(
            @PathVariable UUID auctionId,
            @AuthenticationPrincipal AppUser user
    ) {
        LikeStatusDTO status = auctionReactionService.toggleLike(auctionId, user);
        return ResponseEntity.ok(new ApiResponse<>("Like updated successfully", status));
    }
}
