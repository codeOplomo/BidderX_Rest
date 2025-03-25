package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.*;
import org.anas.bidderx_rest.domain.enums.AuctionType;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.repository.*;
import org.anas.bidderx_rest.service.FeaturedImageService;
import org.anas.bidderx_rest.service.FileStorageService;
import org.anas.bidderx_rest.service.ProductService;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.service.dto.ProductDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppCollectionMapper;
import org.anas.bidderx_rest.service.dto.mapper.ProductMapper;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.anas.bidderx_rest.web.vm.ProductVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final UserService userService;
    private final ProductRepository productRepository;
    private final AppCollectionRepository appCollectionRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final FeaturedImageService featuredImageService;
    private final AppCollectionMapper collectionMapper;
    private final ProductCollectionRepository productCollectionRepository;
    private final FileStorageService fileStorageService;

    public ProductServiceImpl(UserService userService, ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository, FeaturedImageService featuredImageService, AppCollectionMapper collectionMapper, ProductCollectionRepository productCollectionRepository, AppCollectionRepository appCollectionRepository, FileStorageService fileStorageService) {
        this.userService = userService;
        this.productRepository = productRepository;
        this.productCollectionRepository = productCollectionRepository;
        this.appCollectionRepository = appCollectionRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.featuredImageService = featuredImageService;
        this.fileStorageService = fileStorageService;
        this.collectionMapper = collectionMapper;
    }

    @Override
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    @Override
    public List<ProductDTO> getCollectionProducts(UUID collectionId) {
        List<Product> products = productRepository.findByCollectionId(collectionId);
        return products.stream().map(productMapper::toProductDTO).collect(Collectors.toList());
    }

    public void handleProductImages(
            CreateAuctionVM request,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages
    ) {
        System.out.println("main image: " + mainImage);
        System.out.println("additional images: " + additionalImages);
        // Validate main image for new products
        if (request.getExistingProductId() == null) {
            if (mainImage == null || mainImage.isEmpty()) {
                throw new InvalidProductRequestException("Main image is required for new products");
            }

            // Store main image
            String mainImageUrl = fileStorageService.storeFile(mainImage, "products/main");
            request.setProductImageUrl(mainImageUrl);

            // Store additional featured images
            List<String> featuredImageUrls = new ArrayList<>();
            if (additionalImages != null) {
                additionalImages.stream()
                        .filter(img -> !img.isEmpty())
                        .forEach(img -> {
                            String url = fileStorageService.storeFile(img, "products/featured");
                            featuredImageUrls.add(url);
                        });
            }
            request.setFeaturedImageUrls(featuredImageUrls);
        }
    }

    public void handleProductImages(
            ProductVM request,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages
    ) {
            if (mainImage == null || mainImage.isEmpty()) {
                throw new InvalidProductRequestException("Main image is required for new products");
            }

            // Store main image
            String mainImageUrl = fileStorageService.storeFile(mainImage, "products/main");
            request.setImageUrl(mainImageUrl);

            // Store additional featured images
            List<String> featuredImageUrls = new ArrayList<>();
            if (additionalImages != null) {
                additionalImages.stream()
                        .filter(img -> !img.isEmpty())
                        .forEach(img -> {
                            String url = fileStorageService.storeFile(img, "products/featured");
                            featuredImageUrls.add(url);
                        });
            }
            request.setFeaturedImageUrls(featuredImageUrls);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductVM request, MultipartFile mainImage, List<MultipartFile> additionalImages, String username) {
        AppUser owner = userService.findUserByEmail(username);
        Product product = new Product();

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setCondition(request.getCondition());
        product.setManufacturer(request.getManufacturer());
        product.setProductionDate(request.getProductionDate());
        System.out.println("image url: " + request.getImageUrl());
        product.setOwner(owner);

        System.out.println("Category ID: " + request.getCategoryId());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            product.setCategory(category);
        }

        handleProductImages(request, mainImage, additionalImages);
        product.setImageUrl(request.getImageUrl());

        product = productRepository.save(product);

        if (request.getCollectionId() != null) {
            AppCollection collection = appCollectionRepository.findById(request.getCollectionId())
                    .orElseThrow(() -> new CollectionNotFoundException("Collection not found"));

            ProductCollection productCollection = new ProductCollection();
            productCollection.setProduct(product);
            productCollection.setCollection(collection);
            productCollection.setAddedAt(LocalDateTime.now());

            productCollectionRepository.save(productCollection);
        }
        if (request.getFeaturedImageUrls() != null && !request.getFeaturedImageUrls().isEmpty()) {
            featuredImageService.saveBatchFeaturedImages(product.getId(), "product", request.getFeaturedImageUrls());
        }


        return productMapper.toProductDTO(product);
    }

    @Override
    public ProductDTO findProductById(UUID id) {
        Product product = productRepository.findProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        ProductDTO productDTO = productMapper.toProductDTO(product);

        productRepository.findAuctionIdByProductId(id)
                .ifPresent(productDTO::setAuctionId);


        // Set collections if available
        if (product.getProductCollections() != null && !product.getProductCollections().isEmpty()) {
            List<CollectionDTO> collections = product.getProductCollections().stream()
                    .map(pc -> collectionMapper.toCollectionDTO(pc.getCollection()))
                    .collect(Collectors.toList());
            productDTO.setCollections(collections);
        }

        // Get featured images
        List<String> featuredImages = featuredImageService.getProductFeaturedImages(id);
        productDTO.setFeaturedImages(featuredImages);

        return productDTO;
    }

    @Override
    public Page<ProductDTO> findAvailableProductsByEmail(String email, Pageable pageable) {
        AppUser user = userService.findUserByEmail(email);
        Page<Object[]> results = productRepository.findByOwnerEmail(email, pageable);

        return results.map(result -> {
            Product product = (Product) result[0];
            UUID auctionId = (UUID) result[1]; // Get auction ID if available
            ProductDTO dto = productMapper.toProductDTO(product);
            dto.setAuctionId(auctionId);
            return dto;
        });
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
