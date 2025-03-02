package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    Page<Auction> findByOwnerEmail(String email, Pageable pageable);

    Page<Auction> findByOwnerEmailAndStatus(String email, AuctionStatus status, Pageable pageable);

    long countByStatus(AuctionStatus status);

    long count();

    // Updated JPQL to include status filter for liked auctions
    @Query("SELECT DISTINCT a FROM Auction a JOIN a.auctionReactions ar WHERE ar.user.email = :email AND a.status = 'APPROVED'")
    Page<Auction> findByAuctionReactionsUserEmail(@Param("email") String email, Pageable pageable);

    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);

    boolean existsByProductId(UUID id);
}
