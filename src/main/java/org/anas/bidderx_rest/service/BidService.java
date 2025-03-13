package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.BidDTO;
import org.anas.bidderx_rest.web.vm.BidVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BidService {
    Page<BidDTO> getUserBidsForAuction(UUID auctionId, AppUser user, Pageable pageable);
    Page<BidDTO> getBidsForAuction(UUID auctionId, Pageable pageable);
    BidDTO placeBid(BidVM bidVM, AppUser bidder);
}
