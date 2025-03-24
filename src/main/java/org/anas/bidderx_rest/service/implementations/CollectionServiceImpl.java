package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppCollection;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Product;
import org.anas.bidderx_rest.domain.ProductCollection;
import org.anas.bidderx_rest.exceptions.AppCollectionNotFound;
import org.anas.bidderx_rest.exceptions.ProductNotFoundException;
import org.anas.bidderx_rest.exceptions.UserNotFoundException;
import org.anas.bidderx_rest.repository.AppCollectionRepository;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.anas.bidderx_rest.service.CollectionService;
import org.anas.bidderx_rest.service.ProductCollectionService;
import org.anas.bidderx_rest.service.ProductService;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppCollectionMapper;
import org.anas.bidderx_rest.service.dto.mapper.AppUserMapper;
import org.anas.bidderx_rest.web.vm.CollectionVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectionServiceImpl implements CollectionService {
    private final ProductService productService;
    private final ProductCollectionService productCollectionService;
    private final AppCollectionRepository collectionRepository;
    private final AppUserRepository userRepository;
    private final AppCollectionMapper collectionMapper;
    private final AppUserMapper userMapper;


    public CollectionServiceImpl(AppCollectionRepository collectionRepository, AppUserRepository userRepository, AppCollectionMapper collectionMapper, AppUserMapper userMapper, ProductService productService, ProductCollectionService productCollectionService) {
        this.collectionRepository = collectionRepository;
        this.productService = productService;
        this.productCollectionService = productCollectionService;
        this.userRepository = userRepository;
        this.collectionMapper = collectionMapper;
        this.userMapper = userMapper;
    }


    @Override
    @Transactional
    public void removeProductFromCollection(String email, UUID collectionId, UUID productId) {
        // Verify collection exists and belongs to user
        AppCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new AppCollectionNotFound("Collection not found"));

        if (!collection.getAppUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You don't own this collection");
        }

        // Find the ProductCollection entry to remove
        ProductCollection productCollection = productCollectionService.findByCollectionIdAndProductId(collectionId, productId)
                .orElseThrow(() -> new RuntimeException("Product not found in the collection"));

        productCollectionService.delete(productCollection);
    }

    @Override
    @Transactional
    public void addProductsToCollection(String email, UUID collectionId, List<UUID> productIds) {
        // Verify collection exists and belongs to user
        AppCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new AppCollectionNotFound("Collection not found"));

        if (!collection.getAppUser().getEmail().equals(email)) {
            throw new AccessDeniedException("You don't own this collection");
        }

        // Get existing product IDs in collection to avoid duplicates
        Set<UUID> existingProductIds = productCollectionService.findByCollectionId(collectionId)
                .stream()
                .map(pc -> pc.getProduct().getId())
                .collect(Collectors.toSet());

        // Process each product
        for (UUID productId : productIds) {
            if (!existingProductIds.contains(productId)) {
                Product product = productService.findById(productId);

                ProductCollection pc = new ProductCollection();
                pc.setProduct(product);
                pc.setCollection(collection);
                pc.setAddedAt(LocalDateTime.now());

                productCollectionService.save(pc);
            }
        }
    }

    @Override
    public Page<CollectionDTO> getAllCollections(PageRequest pageRequest) {
        Page<AppCollection> collections = collectionRepository.findAll(pageRequest);
        return collections.map(collection -> {
            CollectionDTO collectionDTO = collectionMapper.toCollectionDTO(collection);
            ProfileDTO owner = userMapper.toProfileDTO(collection.getAppUser());
            collectionDTO.setOwner(owner);
            return collectionDTO;
        });
    }


    public CollectionDTO getCollectionById(UUID id) {
        AppCollection collection = collectionRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new AppCollectionNotFound("Collection not found"));
        CollectionDTO collectionDTO = collectionMapper.toCollectionDTO(collection);
        ProfileDTO owner = userMapper.toProfileDTO(collection.getAppUser());
        collectionDTO.setOwner(owner);

        return collectionDTO;
    }

    public Page<CollectionDTO> getCollectionsByEmail(String email, PageRequest pageRequest) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));

        ProfileDTO profile = userMapper.toProfileDTO(user);

        Page<AppCollection> collections = collectionRepository.findByAppUser(user, pageRequest);
        return collections.map(collection -> {
            CollectionDTO dto = collectionMapper.toCollectionDTO(collection);
            dto.setOwner(profile);
            return dto;
        });
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
