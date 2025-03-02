package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.BidDTO;
import org.anas.bidderx_rest.web.vm.BidVM;

import java.util.UUID;

public interface BidService {
    BidDTO placeBid(UUID auctionId, BidVM bidVM, AppUser bidder);
}
