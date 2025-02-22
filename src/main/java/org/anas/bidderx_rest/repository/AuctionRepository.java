package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {


    boolean existsByProductId(UUID id);
}
