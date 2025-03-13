package org.anas.bidderx_rest.service.dto;

import java.math.BigDecimal;
import java.util.Map;

public class AuctionsStateDTO {
    private Long totalAuctions;
    private Long pendingRequests;
    private Long activeAuctions;
    private Long completedAuctions;
    private BigDecimal averageWinningBid;
    private BigDecimal averageBidSpread;
    private Map<String, BigDecimal> revenuePerOwner;
    private Long activeUsers;
    private Long totalRevenue;
    private Long ongoingAuctions;
    private Long totalBids;
    private Long todayRevenue;
    private Long averageBidAmount;
    private Long totalLikes;

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

    public Long getOngoingAuctions() {
        return ongoingAuctions;
    }

    public void setOngoingAuctions(Long ongoingAuctions) {
        this.ongoingAuctions = ongoingAuctions;
    }

    public Long getTotalBids() {
        return totalBids;
    }

    public void setTotalBids(Long totalBids) {
        this.totalBids = totalBids;
    }

    public Long getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(Long todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public Long getAverageBidAmount() {
        return averageBidAmount;
    }

    public void setAverageBidAmount(Long averageBidAmount) {
        this.averageBidAmount = averageBidAmount;
    }

    public Long getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Long totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Long getActiveAuctions() {
        return activeAuctions;
    }

    public void setActiveAuctions(Long activeAuctions) {
        this.activeAuctions = activeAuctions;
    }

    public Long getCompletedAuctions() {
        return completedAuctions;
    }

    public void setCompletedAuctions(Long completedAuctions) {
        this.completedAuctions = completedAuctions;
    }

    public BigDecimal getAverageWinningBid() {
        return averageWinningBid;
    }

    public void setAverageWinningBid(BigDecimal averageWinningBid) {
        this.averageWinningBid = averageWinningBid;
    }

    public BigDecimal getAverageBidSpread() {
        return averageBidSpread;
    }

    public void setAverageBidSpread(BigDecimal averageBidSpread) {
        this.averageBidSpread = averageBidSpread;
    }

    public Map<String, BigDecimal> getRevenuePerOwner() {
        return revenuePerOwner;
    }

    public void setRevenuePerOwner(Map<String, BigDecimal> revenuePerOwner) {
        this.revenuePerOwner = revenuePerOwner;
    }
}
