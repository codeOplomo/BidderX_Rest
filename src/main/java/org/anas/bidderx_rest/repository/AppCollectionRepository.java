package org.anas.bidderx_rest.repository;

import org.anas.bidderx_rest.domain.AppCollection;
import org.anas.bidderx_rest.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppCollectionRepository extends JpaRepository<AppCollection, UUID> {

    @Query("SELECT c FROM AppCollection c LEFT JOIN FETCH c.productCollections pc WHERE c.id = :id")
    Optional<AppCollection> findByIdWithProducts(@Param("id") UUID id);

    List<AppCollection> findByAppUser(AppUser appUser);

    @Query("SELECT c FROM AppCollection c WHERE c.appUser.id = :userId")
    List<AppCollection> findByUserId(@Param("userId") UUID userId);
}
