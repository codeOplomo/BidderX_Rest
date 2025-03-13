package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
//    @Query("SELECT b FROM Bid b JOIN FETCH b.user WHERE b.auction.id = :auctionId ORDER BY b.bidTime DESC")
//    List<Bid> findAllByAuctionId(@Param("auctionId") UUID auctionId, Pageable pageable);

    @Query("SELECT b FROM Bid b JOIN FETCH b.user u " +
            "WHERE b.auction.id = :auctionId " +
            "AND u.email = :userEmail")
    Page<Bid> findAllByAuctionAndUserEmail(
            @Param("auctionId") UUID auctionId,
            @Param("userEmail") String userEmail,
            Pageable pageable
    );

    @Query("SELECT b FROM Bid b JOIN FETCH b.user WHERE b.auction.id = :auctionId")
    Page<Bid> findAllByAuctionId(@Param("auctionId") UUID auctionId, Pageable pageable);

}
