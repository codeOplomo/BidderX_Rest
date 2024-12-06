package org.anas.bidderx_rest.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "bid_amount", nullable = false)
    private BigDecimal bidAmount;

    @Column(name = "bid_time", nullable = false)
    private LocalDateTime bidTime;

    // Constructors
    public Bid() {
    }

    public boolean isValid(Auction auction) {
        // Basic bid attributes validation
        if (this.bidAmount == null
                || this.bidAmount.compareTo(BigDecimal.ZERO) <= 0
                || this.auction == null
                || this.user == null
                || this.bidTime == null) {
            return false;
        }

        // Auction-specific validation
        if (auction == null) {
            return false;
        }

        // Check if auction is active
        if (!auction.isActive()) {
            return false;
        }

        // Bid amount validation against auction parameters
        return this.bidAmount.compareTo(auction.getCurrentPrice()) > 0
                && this.bidAmount.compareTo(auction.getStartingPrice()) >= 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }
}
