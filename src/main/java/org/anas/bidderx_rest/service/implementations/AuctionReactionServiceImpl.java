package org.anas.bidderx_rest.service.implementations;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.AuctionReaction;
import org.anas.bidderx_rest.exceptions.AuctionNotFoundException;
import org.anas.bidderx_rest.repository.AuctionReactionRepository;
import org.anas.bidderx_rest.service.AuctionReactionService;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.dto.LikeStatusDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class AuctionReactionServiceImpl implements AuctionReactionService {

    private final AuctionReactionRepository auctionReactionRepository;
    private final AuctionService auctionService;

    public AuctionReactionServiceImpl(AuctionReactionRepository auctionReactionRepository, AuctionService auctionService) {
        this.auctionReactionRepository = auctionReactionRepository;
        this.auctionService = auctionService;
    }

    public LikeStatusDTO toggleLike(UUID auctionId, AppUser user) {
        System.out.println("Toggling like for auction: " + auctionId + " and user: " + user.getId());
        Auction auction = auctionService.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));
        System.out.println("Found auction: " + auction.getId());

        Optional<AuctionReaction> existingReaction = auctionReactionRepository
                .findByAuctionAndUserAndReactionType(auction, user, "like");
        System.out.println("Existing reaction found: " + existingReaction.isPresent());

        if (existingReaction.isPresent()) {
            System.out.println("Removing existing like");
            auctionReactionRepository.delete(existingReaction.get());
            // Also remove from the Auction's list to keep in-memory state consistent
            auction.getAuctionReactions().remove(existingReaction.get());
        } else {
            System.out.println("Adding new like");
            AuctionReaction reaction = new AuctionReaction();
            reaction.setAuction(auction);
            reaction.setUser(user);
            reaction.setReactionType("like");
            reaction.setReactionTime(LocalDateTime.now());

            // Add the reaction to the Auction's list to maintain bidirectional consistency
            auction.getAuctionReactions().add(reaction);

            AuctionReaction savedReaction = auctionReactionRepository.save(reaction);
            System.out.println("Saved new reaction with ID: " + savedReaction.getId());
        }

        // Get updated status
        long totalLikes = auctionReactionRepository.countByAuction_IdAndReactionType(auctionId, "like");
        boolean hasLiked = !existingReaction.isPresent();
        return new LikeStatusDTO(totalLikes, hasLiked);
    }
}
