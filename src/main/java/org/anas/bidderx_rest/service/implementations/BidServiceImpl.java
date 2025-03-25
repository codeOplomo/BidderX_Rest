package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.Bid;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.anas.bidderx_rest.domain.enums.AuctionType;
import org.anas.bidderx_rest.exceptions.AuctionNotFoundException;
import org.anas.bidderx_rest.exceptions.InsufficientFundsException;
import org.anas.bidderx_rest.exceptions.InvalidBidException;
import org.anas.bidderx_rest.exceptions.UserNotFoundException;
import org.anas.bidderx_rest.repository.BidRepository;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.BidService;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.WalletService;
import org.anas.bidderx_rest.service.dto.BidDTO;
import org.anas.bidderx_rest.service.dto.WalletDTO;
import org.anas.bidderx_rest.service.dto.mapper.BidMapper;
import org.anas.bidderx_rest.web.vm.BidVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BidServiceImpl implements BidService {
    private final AuctionService auctionService;
    private final WalletService walletService;
    private final BidRepository bidRepository;
    private final BidMapper bidMapper;
    private final UserService userService;

    public BidServiceImpl(AuctionService auctionService, BidRepository bidRepository, BidMapper bidMapper, WalletService walletService, UserService userService) {
        this.auctionService = auctionService;
        this.userService = userService;
        this.walletService = walletService;
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
    }


    @Transactional
    public BidDTO placeBid(BidVM bidVM, AppUser bidder) {
        Auction auction = auctionService.findById(bidVM.getAuctionId())
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        AppUser managedBidder = userService.findUserByEmail(bidder.getEmail());

        WalletDTO wallet = walletService.findByUser(bidder);

        validateBid(auction, bidVM.getBidAmount(), bidder);

        walletService.withdrawFunds(wallet.getId(), bidVM.getBidAmount());

        Bid newBid = new Bid();
        newBid.setAuction(auction);
        newBid.setUser(managedBidder);
        newBid.setBidAmount(bidVM.getBidAmount());
        newBid.setBidTime(LocalDateTime.now());

        Bid savedBid = bidRepository.save(newBid);

        updateAuctionCurrentPrice(auction, newBid.getBidAmount());

        if (auction.getAuctionType() == AuctionType.INSTANT) {
            long bidCount = bidRepository.countByAuctionId(auction.getId());
            if (bidCount == 1) {
                auction.setEndTime(LocalDateTime.now());
                auction.setStatus(AuctionStatus.ENDED);
                auction.setWinnerId(bidder.getId());
                auctionService.save(auction);
            }
        }
        return bidMapper.toBidDTO(savedBid);
    }

    @Override
    public Page<BidDTO> getUserBidsForAuction(UUID auctionId, AppUser user, Pageable pageable) {
        // Validate auction exists
        auctionService.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        // Fetch bids with user verification
        Page<Bid> bidsPage = bidRepository.findAllByAuctionAndUserEmail(
                auctionId,
                user.getEmail(),
                pageable
        );

        return bidsPage.map(bid -> {
            // Additional validation if needed
            if (!bid.getUser().getEmail().equals(user.getEmail())) {
                throw new InvalidBidException("Bid access violation");
            }
            return bidMapper.toBidDTO(bid);
        });
    }

    public Page<BidDTO> getBidsForAuction(UUID auctionId, Pageable pageable) {
        Page<Bid> bidsPage = bidRepository.findAllByAuctionId(auctionId, pageable);
        return bidsPage.map(bidMapper::toBidDTO);
    }


    private void validateBid(Auction auction, BigDecimal bidAmount, AppUser bidder) {
        // Existing owner and status checks
        if (auction.getStatus() != AuctionStatus.APPROVED) {
            throw new InvalidBidException("Auction is not active for bidding");
        }

        if (auction.getOwner().getId().equals(bidder.getId())) {
            throw new InvalidBidException("Auction owner cannot bid on their own auction");
        }

        LocalDateTime now = LocalDateTime.now();

        // Modified time validation
        if (auction.getAuctionType() == AuctionType.TIMED) {
            // Validate time window for timed auctions
            if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
                throw new InvalidBidException("Auction is not currently active");
            }
        } else if (auction.getAuctionType() == AuctionType.INSTANT) {
            // Additional check for instant auctions
            if (auction.getEndTime() != null) {
                throw new InvalidBidException("Instant auction has already ended");
            }
        }

        // Existing bid amount validation
        if (bidAmount.compareTo(auction.getCurrentPrice()) <= 0) {
            throw new InvalidBidException("Bid amount must be higher than current price of "
                    + auction.getCurrentPrice());
        }
    }

    private void updateAuctionCurrentPrice(Auction auction, BigDecimal newBid) {
        auction.setCurrentPrice(newBid);
        auctionService.save(auction);
    }

}
