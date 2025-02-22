package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppCollection;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.exceptions.AppCollectionNotFound;
import org.anas.bidderx_rest.exceptions.UserNotFoundException;
import org.anas.bidderx_rest.repository.AppCollectionRepository;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.anas.bidderx_rest.service.CollectionService;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppCollectionMapper;
import org.anas.bidderx_rest.web.vm.CollectionVM;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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



    public CollectionDTO getCollectionById(UUID id) {
        AppCollection collection = collectionRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new AppCollectionNotFound("Collection not found"));

        return collectionMapper.toCollectionDTO(collection);
    }

    public List<CollectionDTO> getCollectionsByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
        List<AppCollection> collections = collectionRepository.findByAppUser(user);
        return collections.stream()
                .map(collectionMapper::toCollectionDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void uploadShowcaseImage(UUID collectionId, String imageUrl) {
        // Fetch the user's showcase collection
        AppCollection showcaseCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Showcase collection not found"));

        System.out.println("Showcase collection: " + showcaseCollection);
        // Update the collection's image URL
        showcaseCollection.setImageUrl(imageUrl);
        collectionRepository.save(showcaseCollection);
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

}
