package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
//    Optional<Transaction> findByUser(AppUser user);
}
