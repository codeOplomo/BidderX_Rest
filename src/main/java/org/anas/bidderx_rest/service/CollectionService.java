package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppCollection;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.web.vm.CollectionVM;

import java.util.List;
import java.util.UUID;

public interface CollectionService {
    CollectionDTO createCollection(String username, CollectionVM createVM);

    CollectionDTO getCollectionById(UUID id);

    void uploadShowcaseImage(UUID collectionId, String imageUrl);

    List<CollectionDTO> getCollectionsByEmail(String email);
}
