package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUser(AppUser user);

}
