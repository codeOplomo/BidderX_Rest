package org.anas.bidderx_rest.web.vm;

import jakarta.validation.constraints.NotBlank;

public class RejectAuctionVM {
    @NotBlank(message = "Reason is required for rejection")
    private String reason;

    // Getters and Setters
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
