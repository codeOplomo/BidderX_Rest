package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID>, JpaSpecificationExecutor<AppUser> {
    Optional<AppUser> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u, COALESCE(SUM(a.currentPrice), 0) " +
            "FROM AppUser u LEFT JOIN Auction a ON a.owner = u " +
            "WHERE (LOWER(u.profileIdentifier) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND a.status = 'ENDED' " +
            "GROUP BY u")
    Page<Object[]> searchUsersWithRevenue(@Param("query") String query, Pageable pageable);
}
