package org.anas.bidderx_rest.service.dto;

import java.math.BigDecimal;

public class OwnerRankingDTO {
    private ProfileDTO owner;
    private BigDecimal revenue;

    // Constructor, Getters, Setters
    public OwnerRankingDTO(ProfileDTO owner, BigDecimal revenue) {
        this.owner = owner;
        this.revenue = revenue;
    }

public ProfileDTO getOwner() {
        return owner;
    }

public void setOwner(ProfileDTO owner) {
        this.owner = owner;
    }

public BigDecimal getRevenue() {
        return revenue;
    }

public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
