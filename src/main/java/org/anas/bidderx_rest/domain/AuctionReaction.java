package org.anas.bidderx_rest.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auction_reactions")
public class AuctionReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "reaction_type", nullable = false)
    private String reactionType; // Example: "like", "dislike", etc.

    @Column(name = "reaction_time", nullable = false)
    private LocalDateTime reactionTime;

    // Constructors
    public AuctionReaction() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }

    public LocalDateTime getReactionTime() {
        return reactionTime;
    }

    public void setReactionTime(LocalDateTime reactionTime) {
        this.reactionTime = reactionTime;
    }
}
