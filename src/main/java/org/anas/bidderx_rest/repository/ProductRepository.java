package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByOwner(AppUser appUser);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Auction a WHERE a.product.id = :productId")
    boolean existsByProductIdInAuction(@Param("productId") UUID productId);
}
