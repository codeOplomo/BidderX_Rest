package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.ProductCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductCollectionRepository extends JpaRepository<ProductCollection, UUID> {
}
