package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.*;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.repository.*;
import org.anas.bidderx_rest.service.FeaturedImageService;
import org.anas.bidderx_rest.service.ProductService;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.service.dto.ProductDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppCollectionMapper;
import org.anas.bidderx_rest.service.dto.mapper.ProductMapper;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final AppUserRepository userRepository;
    private final ProductRepository productRepository;
    private final AppCollectionRepository appCollectionRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final FeaturedImageService featuredImageService;
    private final AppCollectionMapper collectionMapper;
    private final ProductCollectionRepository productCollectionRepository;

    public ProductServiceImpl(AppUserRepository userRepository, ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository, FeaturedImageService featuredImageService, AppCollectionMapper collectionMapper, ProductCollectionRepository productCollectionRepository, AppCollectionRepository appCollectionRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productCollectionRepository = productCollectionRepository;
        this.appCollectionRepository = appCollectionRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.featuredImageService = featuredImageService;
        this.collectionMapper = collectionMapper;
    }


    @Override
    public ProductDTO findProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        ProductDTO productDTO = productMapper.toProductDTO(product);

        if (product.getProductCollections() != null && !product.getProductCollections().isEmpty()) {
            List<CollectionDTO> collections = product.getProductCollections().stream()
                    .map(pc -> collectionMapper.toCollectionDTO(pc.getCollection()))
                    .collect(Collectors.toList());
            productDTO.setCollections(collections);
        }

        List<String> featuredImages = featuredImageService.getProductFeaturedImages(id);
        productDTO.setFeaturedImages(featuredImages);
        return productDTO;
    }

    @Override
    public List<ProductDTO> findAvailableProductsByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
        List<Product> products = productRepository.findByOwner(user);
        return products.stream()
                .map(productMapper::toProductDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public Product getOrCreateProduct(CreateAuctionVM request, AppUser owner) {
        if (request.getExistingProductId() != null) {
            // Retrieve the existing product
            Product product = productRepository.findById(request.getExistingProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));
            if (isProductInAuction(product.getId())) {
                throw new ProductAlreadyInAuctionException("Product is already in an auction");
            }
            return product;
        }

        // Fetch the category from the request
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        // Create a new product with the provided details and category
        Product product = new Product();
        product.setTitle(request.getProductTitle());
        product.setDescription(request.getProductDescription());
        product.setCondition(request.getCondition());
        product.setManufacturer(request.getManufacturer());
        product.setProductionDate(request.getProductionDate());
        product.setImageUrl(request.getProductImageUrl());
        product.setOwner(owner);
        product.setCategory(category);

        product = productRepository.save(product);

        // Handle product collection relation if a collectionId is provided
        if (request.getCollectionId() != null) {
            // Retrieve the AppCollection entity (you might need an AppCollectionRepository for this)
            AppCollection collection = appCollectionRepository.findById(request.getCollectionId())
                    .orElseThrow(() -> new CollectionNotFoundException("Collection not found"));

            ProductCollection productCollection = new ProductCollection();
            productCollection.setProduct(product);
            productCollection.setCollection(collection);
            productCollection.setAddedAt(LocalDateTime.now());

            // Save the product collection relation (assuming you have a repository for ProductCollection)
            productCollectionRepository.save(productCollection);
        }

        if (request.getFeaturedImageUrls() != null && !request.getFeaturedImageUrls().isEmpty()) {
            featuredImageService.saveBatchFeaturedImages(product.getId(), "product", request.getFeaturedImageUrls());
        }

        return product;
    }


    @Override
    public boolean isProductInAuction(UUID productId) {
        return productRepository.existsByProductIdInAuction(productId);
    }

}
