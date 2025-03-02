package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.AuctionReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface AuctionReactionRepository extends JpaRepository<AuctionReaction, UUID> {
    Optional<AuctionReaction> findByAuctionAndUserAndReactionType(Auction auction, AppUser user, String reactionType);

    long countByAuction_IdAndReactionType(UUID auctionId, String reactionType);

    boolean existsByAuction_IdAndUser_IdAndReactionType(UUID auctionId, UUID userId, String reactionType);

}
