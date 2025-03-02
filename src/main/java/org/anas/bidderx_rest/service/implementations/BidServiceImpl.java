package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.Bid;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.anas.bidderx_rest.exceptions.AuctionNotFoundException;
import org.anas.bidderx_rest.exceptions.InvalidBidException;
import org.anas.bidderx_rest.repository.BidRepository;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.BidService;
import org.anas.bidderx_rest.service.dto.BidDTO;
import org.anas.bidderx_rest.web.vm.BidVM;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BidServiceImpl{
//    private final AuctionService auctionService;
//    private final BidRepository bidRepository;
////    private final BidMapper bidMapper;
//
//    public BidServiceImpl(AuctionService auctionService, BidRepository bidRepository) {
//        this.auctionService = auctionService;
//        this.bidRepository = bidRepository;
//    }
//
//    @Transactional
//    public BidDTO placeBid(UUID auctionId, BidVM bidVM, AppUser bidder) {
//        Auction auction = auctionService.findById(auctionId)
//                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));
//
////        validateBid(auction, bidVM.getBidAmount(), bidder);
////
////        Bid newBid = modelMapper.map(bidVM, Bid.class);
////        newBid.setAuction(auction);
////        newBid.setUser(bidder);
////        newBid.setBidTime(LocalDateTime.now());
////
////        Bid savedBid = bidRepository.save(newBid);
////
////        updateAuctionCurrentPrice(auction, newBid.getBidAmount());
////
////        return mapToBidDTO(savedBid);
//    }
//
//    private void validateBid(Auction auction, BigDecimal bidAmount, AppUser bidder) {
//        // Existing validation logic from previous answer
//        if (auction.getStatus() != AuctionStatus.APPROVED) {
//            throw new InvalidBidException("Auction is not active for bidding");
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
//            throw new InvalidBidException("Auction is not currently active");
//        }
//
//        if (auction.getOwner().getId().equals(bidder.getId())) {
//            throw new InvalidBidException("Auction owner cannot bid on their own auction");
//        }
//
//        if (bidAmount.compareTo(auction.getCurrentPrice()) <= 0) {
//            throw new InvalidBidException("Bid amount must be higher than current price of "
//                    + auction.getCurrentPrice());
//        }
//    }
//
//    private void updateAuctionCurrentPrice(Auction auction, BigDecimal newBid) {
//        auction.setCurrentPrice(newBid);
////        auctionService.save(auction);
//    }
//
//    private BidDTO mapToBidDTO(Bid bid) {
////        return modelMapper.map(bid, BidDTO.class);
//    }
}
