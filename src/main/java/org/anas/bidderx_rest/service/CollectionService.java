package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppCollection;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.web.vm.CollectionVM;

import java.util.UUID;

public interface CollectionService {
    CollectionDTO createCollection(String username, CollectionVM createVM);

    CollectionDTO getCollectionById(UUID id);
}
