package org.anas.bidderx_rest.web.vm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class BidVM {
    @NotNull
    @Positive
    private BigDecimal bidAmount;

    public @NotNull @Positive BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(@NotNull @Positive BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }
}
