package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.LikeStatusDTO;

import java.util.UUID;

public interface AuctionReactionService {
    LikeStatusDTO toggleLike(UUID auctionId, AppUser user);
}
