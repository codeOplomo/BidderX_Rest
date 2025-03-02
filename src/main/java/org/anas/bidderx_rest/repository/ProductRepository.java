package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Product;
import org.anas.bidderx_rest.domain.enums.AuctionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p WHERE p.owner.email = :email")
    Page<Product> findByOwnerEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Auction a WHERE a.product.id = :productId")
    boolean existsByProductIdInAuction(@Param("productId") UUID productId);

    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findProductById(@Param("productId") UUID productId);

    @Query("SELECT a.auctionType FROM Auction a WHERE a.product.id = :productId")
    Optional<AuctionType> findAuctionTypeByProductId(@Param("productId") UUID productId);

    @Query("SELECT a.id FROM Auction a WHERE a.product.id = :productId")
    Optional<UUID> findAuctionIdByProductId(@Param("productId") UUID productId);


}
