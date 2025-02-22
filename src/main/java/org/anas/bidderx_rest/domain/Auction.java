package org.anas.bidderx_rest.domain;

import jakarta.persistence.*;
import org.anas.bidderx_rest.domain.enums.AuctionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "auction_type", nullable = false)
    private AuctionType auctionType;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionReaction> auctionReactions = new ArrayList<>();

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "starting_price", nullable = false)
    private BigDecimal startingPrice;

    @Column(name = "current_price", nullable = false)
    private BigDecimal currentPrice;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    // Helper method to add a bid
    public void addBid(Bid bid) {
        if (bid != null) {
            this.bids.add(bid);
            bid.setAuction(this);
        }
    }

    // Helper method to remove a bid
    public void removeBid(Bid bid) {
        if (bid != null) {
            this.bids.remove(bid);
            bid.setAuction(null);
        }
    }

    // Helper method to add an auction reaction
    public void addAuctionReaction(AuctionReaction reaction) {
        if (reaction != null) {
            this.auctionReactions.add(reaction);
            reaction.setAuction(this);
        }
    }

    // Helper method to remove an auction reaction
    public void removeAuctionReaction(AuctionReaction reaction) {
        if (reaction != null) {
            this.auctionReactions.remove(reaction);
            reaction.setAuction(null);
        }
    }

    // Helper method to check if auction is active
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

    // Helper method to update current price
    public void updateCurrentPrice(BigDecimal newPrice) {
        if (newPrice != null && newPrice.compareTo(this.currentPrice) > 0) {
            this.currentPrice = newPrice;
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(AuctionType auctionType) {
        this.auctionType = auctionType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public List<AuctionReaction> getAuctionReactions() {
        return auctionReactions;
    }

    public void setAuctionReactions(List<AuctionReaction> auctionReactions) {
        this.auctionReactions = auctionReactions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }
}

