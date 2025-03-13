package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID>, JpaSpecificationExecutor<Auction> {

    Page<Auction> findByWinnerIdAndStatus(UUID winnerId, AuctionStatus status, Pageable pageable);

    Page<Auction> findByOwnerEmail(String email, Pageable pageable);

    Page<Auction> findByOwnerEmailAndStatus(String email, AuctionStatus status, Pageable pageable);

    long countByStatus(AuctionStatus status);

    @Query("SELECT COUNT(a) FROM Auction a WHERE a.status = 'APPROVED' AND a.startTime <= CURRENT_TIMESTAMP AND a.endTime >= CURRENT_TIMESTAMP")
    long countActiveAuctions();

    // Count completed auctions
    @Query("SELECT COUNT(a) FROM Auction a WHERE a.status = 'COMPLETED'")
    long countCompletedAuctions();

    // Average winning bid from completed auctions
    @Query("SELECT AVG(a.currentPrice) FROM Auction a WHERE a.status = 'COMPLETED'")
    BigDecimal findAverageWinningBid();

    // Count total bids across all auctions
    @Query("SELECT COUNT(b) FROM Bid b")
    long countTotalBids();

    long count();

    // Updated JPQL to include status filter for liked auctions
    @Query("SELECT DISTINCT a FROM Auction a JOIN a.auctionReactions ar WHERE ar.user.email = :email AND a.status = 'APPROVED'")
    Page<Auction> findByAuctionReactionsUserEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT COUNT(ar) FROM AuctionReaction ar WHERE ar.reactionType = 'like'")
    long countTotalLikes();

    // Revenue per owner – group completed auctions by owner and sum up currentPrice
    @Query("SELECT a.owner.email, SUM(a.currentPrice) FROM Auction a WHERE a.status = 'COMPLETED' GROUP BY a.owner.email")
    List<Object[]> findRevenuePerOwner();

    // Count ongoing auctions (regardless of status, based on time window)
    @Query("SELECT COUNT(a) FROM Auction a WHERE a.startTime <= CURRENT_TIMESTAMP AND a.endTime >= CURRENT_TIMESTAMP")
    long countOngoingAuctions();

    // Today revenue: sum currentPrice for auctions that completed today
    @Query("SELECT SUM(a.currentPrice) FROM Auction a WHERE a.status = 'COMPLETED' AND a.endTime BETWEEN :start AND :end")
    BigDecimal findTodayRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Average bid amount from all bids
    @Query("SELECT AVG(b.bidAmount) FROM Bid b")
    BigDecimal findAverageBidAmount();

    Page<Auction> findByStatus(AuctionStatus status, Pageable pageable);

    boolean existsByProductId(UUID id);

    @Query("SELECT a FROM Auction a " +
            "WHERE LOWER(a.title) LIKE LOWER(:term) OR " +
            "LOWER(a.description) LIKE LOWER(:term) OR " +
            "LOWER(a.product.title) LIKE LOWER(:term) " +
            "ORDER BY a.startTime DESC")
    List<Auction> findBySearchTerm(@Param("term") String term, Pageable pageable);

    default List<Auction> findBySearchTerm(String term, int limit) {
        return findBySearchTerm(term, PageRequest.of(0, limit));
    }
}
