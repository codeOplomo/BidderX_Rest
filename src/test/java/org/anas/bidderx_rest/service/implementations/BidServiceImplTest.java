package org.anas.bidderx_rest.service.implementations;


import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.anas.bidderx_rest.exceptions.AuctionNotFoundException;
import org.anas.bidderx_rest.exceptions.InsufficientFundsException;
import org.anas.bidderx_rest.exceptions.InvalidBidException;
import org.anas.bidderx_rest.repository.BidRepository;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.WalletService;
import org.anas.bidderx_rest.service.dto.WalletDTO;
import org.anas.bidderx_rest.service.dto.mapper.BidMapper;
import org.anas.bidderx_rest.web.vm.BidVM;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BidServiceImplTest {

    @InjectMocks
    private BidServiceImpl bidService;

    @Mock
    private AuctionService auctionService;

    @Mock
    private WalletService walletService;

    @Mock
    private UserService userService;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private BidMapper bidMapper;
    private UUID auctionId;
    private BidVM bidVM;
    private AppUser bidder;

    @BeforeEach
    void setUp() {
        auctionId = UUID.randomUUID();

        bidVM = new BidVM();
        bidVM.setAuctionId(auctionId);
        bidVM.setBidAmount(new BigDecimal("100.00"));

        bidder = new AppUser();
        bidder.setEmail("test@example.com");
    }

    @Test
    void testPlaceBid_AuctionNotFound() {
        // Mock the auction service to return empty (auction not found)
        when(auctionService.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Assert that calling placeBid throws AuctionNotFoundException
        Assertions.assertThrows(AuctionNotFoundException.class, () -> bidService.placeBid(bidVM, bidder));

        // Verify interactions
        verify(auctionService, times(1)).findById(any(UUID.class));
    }

    @Test
    void testPlaceBid_InsufficientFunds() {
        // Create auction owner (different from bidder)
        AppUser auctionOwner = new AppUser();
        auctionOwner.setId(UUID.randomUUID());
        auctionOwner.setEmail("owner@example.com");

        // Mock auction
        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setStatus(AuctionStatus.APPROVED);
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(1));
        auction.setCurrentPrice(new BigDecimal("100.00"));
        auction.setOwner(auctionOwner);

        when(auctionService.findById(any(UUID.class))).thenReturn(Optional.of(auction));
        when(userService.findUserByEmail(anyString())).thenReturn(bidder);

        // Create wallet with insufficient funds
        WalletDTO wallet = new WalletDTO();
        wallet.setId(UUID.randomUUID()); // ensure wallet has an ID
        wallet.setBalance(new BigDecimal("50.00"));
        when(walletService.findByUser(any())).thenReturn(wallet);

        // Set a bid amount that passes bid validation (greater than current price)
        bidVM.setBidAmount(new BigDecimal("150.00"));

        // Stub withdrawFunds to throw InsufficientFundsException for insufficient balance
        when(walletService.withdrawFunds(wallet.getId(), bidVM.getBidAmount()))
                .thenThrow(new InsufficientFundsException("Insufficient balance"));

        // Verify that placing the bid throws InsufficientFundsException
        Assertions.assertThrows(InsufficientFundsException.class, () -> bidService.placeBid(bidVM, bidder));
    }

    @Test
    void testPlaceBid_InvalidBid() {
        // Create a mock auction with a higher current price than the bid amount
        Auction auction = new Auction();
        auction.setId(auctionId);
        auction.setStatus(AuctionStatus.APPROVED);
        auction.setCurrentPrice(new BigDecimal("200.00")); // Higher than bidVM's bid amount

        // Set a valid auction timeframe so that the auction is active
        auction.setStartTime(LocalDateTime.now().minusHours(1));
        auction.setEndTime(LocalDateTime.now().plusHours(1));

        // Set an auction owner that is different from the bidder
        AppUser auctionOwner = new AppUser();
        auctionOwner.setId(UUID.randomUUID());
        auctionOwner.setEmail("owner@example.com");
        auction.setOwner(auctionOwner);

        when(auctionService.findById(any(UUID.class))).thenReturn(Optional.of(auction));

        // Mock user retrieval (bidder is already set up in setUp())
        when(userService.findUserByEmail(anyString())).thenReturn(bidder);

        // Mock wallet with sufficient funds
        WalletDTO wallet = new WalletDTO();
        wallet.setBalance(new BigDecimal("500.00"));
        when(walletService.findByUser(any())).thenReturn(wallet);

        // Assert that calling placeBid throws InvalidBidException because the bid amount (100.00)
        // is lower than the auction's current price (200.00)
        Assertions.assertThrows(InvalidBidException.class, () -> bidService.placeBid(bidVM, bidder));
    }

}