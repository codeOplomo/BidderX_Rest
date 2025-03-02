package org.anas.bidderx_rest.service.dto;

public class AuctionsStateDTO {
    private Long totalAuctions;
    private Long pendingRequests;
    private Long activeUsers;
    private Long totalRevenue;

    public AuctionsStateDTO() {}

    public AuctionsStateDTO(Long totalAuctions, Long pendingRequests, Long activeUsers, Long totalRevenue) {
        this.totalAuctions = totalAuctions;
        this.pendingRequests = pendingRequests;
        this.activeUsers = activeUsers;
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Long totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalAuctions() {
        return totalAuctions;
    }

    public void setTotalAuctions(Long totalAuctions) {
        this.totalAuctions = totalAuctions;
    }

    public Long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(Long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public Long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Long activeUsers) {
        this.activeUsers = activeUsers;
    }
}
