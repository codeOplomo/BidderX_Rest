package org.anas.bidderx_rest.service.implementations;

import org.anas.bidderx_rest.domain.AppCollection;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.exceptions.AppCollectionNotFound;
import org.anas.bidderx_rest.repository.AppCollectionRepository;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.anas.bidderx_rest.service.CollectionService;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppCollectionMapper;
import org.anas.bidderx_rest.web.vm.CollectionVM;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollectionServiceImpl implements CollectionService {
    private final AppCollectionRepository collectionRepository;
    private final AppUserRepository userRepository;
    private final AppCollectionMapper collectionMapper;


    public CollectionServiceImpl(AppCollectionRepository collectionRepository, AppUserRepository userRepository, AppCollectionMapper collectionMapper) {
        this.collectionRepository = collectionRepository;
        this.userRepository = userRepository;
        this.collectionMapper = collectionMapper;
    }

    public String uploadShowcaseImage(UUID collectionId, String imageUrl) {
AppCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new AppCollectionNotFound("Collection not found"));

        collection.setImageUrl(imageUrl);
        collectionRepository.save(collection);

        return imageUrl;
    }


    public CollectionDTO createCollection(String username, CollectionVM createVM) {
        AppUser user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AppCollection collection = new AppCollection();
        collection.setName(createVM.getName());
        collection.setDescription(createVM.getDescription());
        collection.setAppUser(user);

        AppCollection savedCollection = collectionRepository.save(collection);

        // Map the entity to a DTO
        return collectionMapper.toCollectionDTO(savedCollection);
    }


    public CollectionDTO getCollectionById(UUID id) {
        AppCollection collection = collectionRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new AppCollectionNotFound("Collection not found"));

        return collectionMapper.toCollectionDTO(collection);
    }
}
