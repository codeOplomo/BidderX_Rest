package org.anas.bidderx_rest.web.vm;

import java.math.BigDecimal;

public class DepositVM {
    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
