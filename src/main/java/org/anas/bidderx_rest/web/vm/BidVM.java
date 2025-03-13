package org.anas.bidderx_rest.web.vm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class BidVM {
    @NotNull
    @Positive
    private BigDecimal bidAmount;

    @NotNull
    private UUID auctionId;


    public @NotNull @Positive BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(@NotNull @Positive BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public @NotNull UUID getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(@NotNull UUID auctionId) {
        this.auctionId = auctionId;
    }
}
