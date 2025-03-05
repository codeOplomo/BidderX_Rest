package org.anas.bidderx_rest.service.dto;

import org.anas.bidderx_rest.domain.enums.WalletType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class WalletDTO {
    private UUID id;
    private WalletType type;
    private String currencyCode;
    private BigDecimal balance;
    private String userId;
    private List<TransactionDTO> transactions;
    private String checkoutUrl;

    public WalletDTO() {
    }

    public WalletDTO(UUID id, BigDecimal balance, String userId, List<TransactionDTO> transactions) {
        this.id = id;
        this.balance = balance;
        this.userId = userId;
        this.transactions = transactions;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public WalletType getType() {
        return type;
    }

    public void setType(WalletType type) {
        this.type = type;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
