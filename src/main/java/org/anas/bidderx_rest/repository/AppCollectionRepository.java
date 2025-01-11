package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.AppCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AppCollectionRepository extends JpaRepository<AppCollection, UUID> {

    @Query("SELECT c FROM AppCollection c LEFT JOIN FETCH c.productCollections pc WHERE c.id = :id")
    Optional<AppCollection> findByIdWithProducts(@Param("id") UUID id);

}
