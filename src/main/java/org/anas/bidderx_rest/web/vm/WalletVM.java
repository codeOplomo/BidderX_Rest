package org.anas.bidderx_rest.web.vm;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class WalletVM {
    @NotBlank
    private String type;
    private String currencyCode;
    private BigDecimal depositAmount;

    public WalletVM() {
    }

    public WalletVM(String currencyCode, BigDecimal depositAmount) {
        this.currencyCode = currencyCode;
        this.depositAmount = depositAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public @NotBlank String getType() {
        return type;
    }

    public void setType(@NotBlank String type) {
        this.type = type;
    }
}
