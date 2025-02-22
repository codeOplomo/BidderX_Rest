package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.Category;
import org.anas.bidderx_rest.domain.Product;
import org.anas.bidderx_rest.domain.enums.AuctionType;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.repository.AuctionRepository;
import org.anas.bidderx_rest.repository.CategoryRepository;
import org.anas.bidderx_rest.repository.ProductRepository;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.FileStorageService;
import org.anas.bidderx_rest.service.ProductService;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.anas.bidderx_rest.service.dto.mapper.AuctionMapper;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;
    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public AuctionServiceImpl(
            AuctionRepository auctionRepository,
            AuctionMapper auctionMapper,
            ProductService productService,
            FileStorageService fileStorageService
    ) {
        this.auctionRepository = auctionRepository;
        this.auctionMapper = auctionMapper;
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    public void handleProductImages(
            CreateAuctionVM request,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages
    ) {
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

    @Transactional
    public AuctionDTO createAuction(CreateAuctionVM request, MultipartFile mainImage, List<MultipartFile> additionalImages, @AuthenticationPrincipal AppUser authenticatedUser) {
        if (request.getExistingProductId() == null) {
            handleProductImages(request, mainImage, additionalImages);
        }

        validateAuctionRules(request);

        // Get or create product
        Product product = productService.getOrCreateProduct(request, authenticatedUser);

        // Validate product ownership
        if (!product.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new UnauthorizedOperationException("You can only auction products you own");
        }

        // Create and save auction
        Auction auction = createAuctionEntity(request, product, authenticatedUser);
        auction = auctionRepository.save(auction);

        return auctionMapper.toDTO(auction);
    }


    private Auction createAuctionEntity(CreateAuctionVM request, Product product, AppUser owner) {
        Auction auction = new Auction();
        auction.setProduct(product);
        auction.setOwner(owner);
        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartTime(request.getStartTime());
        auction.setStartingPrice(request.getStartingPrice());
        auction.setCurrentPrice(request.getStartingPrice());

        // Calculate and set the endTime for non-instant auctions
        if (request.isInstantAuction()) {
            auction.setAuctionType(AuctionType.INSTANT);
            auction.setEndTime(null); // No end time for instant auctions
        } else {
            auction.setAuctionType(AuctionType.TIMED);
            if (request.getAuctionDurationInHours() == null || request.getAuctionDurationInHours() <= 0) {
                throw new InvalidAuctionException("Auction duration must be greater than zero for non-instant auctions");
            }
            auction.setEndTime(request.getStartTime().plusHours(request.getAuctionDurationInHours()));
        }

        return auction;
    }

    private void validateAuctionRules(CreateAuctionVM request) {
        // 1. Validate auction duration for non-instant auctions
        if (!request.isInstantAuction()) {
            if (request.getAuctionDurationInHours() == null || request.getAuctionDurationInHours() <= 0) {
                throw new InvalidAuctionException(
                        "Auction duration must be specified and greater than zero for non-instant auctions"
                );
            }
        }

        // 2. Validate start time (applies to all auctions)
        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidAuctionException("Start time cannot be in the past");
        }

        // 3. Validate starting price (example: must be >= 0)
        if (request.getStartingPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAuctionException("Starting price cannot be negative");
        }
    }
}
