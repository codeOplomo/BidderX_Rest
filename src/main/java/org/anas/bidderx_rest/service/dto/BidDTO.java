package org.anas.bidderx_rest.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BidDTO {
    private UUID bidId;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;
    private UUID auctionId;
    private ProfileDTO bidder;
//    private String bidderEmail;

    public UUID getBidId() {
        return bidId;
    }

    public void setBidId(UUID bidId) {
        this.bidId = bidId;
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

    public UUID getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(UUID auctionId) {
        this.auctionId = auctionId;
    }


    public ProfileDTO getBidder() {
        return bidder;
    }

    public void setBidder(ProfileDTO bidder) {
        this.bidder = bidder;
    }
}
