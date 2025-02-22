package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.FeaturedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeaturedImageRepository extends JpaRepository<FeaturedImage, UUID> {
    // Custom queries if needed
    List<FeaturedImage> findByEntityTypeAndEntityId(String entityType, UUID entityId);
}
