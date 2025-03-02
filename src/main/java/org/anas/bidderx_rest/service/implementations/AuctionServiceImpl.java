package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.Product;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.anas.bidderx_rest.domain.enums.AuctionType;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.repository.AuctionRepository;
import org.anas.bidderx_rest.service.AuctionService;
import org.anas.bidderx_rest.service.FileStorageService;
import org.anas.bidderx_rest.service.ProductService;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.anas.bidderx_rest.service.dto.AuctionsStateDTO;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.service.dto.mapper.AuctionMapper;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionServiceImpl implements AuctionService {
    private final UserService userService;
    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;
    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public AuctionServiceImpl(
            UserService userService,
            AuctionRepository auctionRepository,
            AuctionMapper auctionMapper,
            ProductService productService,
            FileStorageService fileStorageService
    ) {
        this.userService = userService;
        this.auctionRepository = auctionRepository;
        this.auctionMapper = auctionMapper;
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }


    @Override
    public AuctionDTO getAuctionById(UUID auctionId, AppUser user) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        AuctionDTO dto = auctionMapper.toDTO(auction);
        setLikeStatus(auction, dto, user);
        return dto;
    }

    private void setLikeStatus(Auction auction, AuctionDTO dto, AppUser user) {
        if (user != null) {
            boolean isLiked = auction.getAuctionReactions().stream()
                    .anyMatch(reaction -> reaction.getUser().getId().equals(user.getId()));
            dto.setLikedByCurrentUser(isLiked);
        } else {
            dto.setLikedByCurrentUser(false); // Default for non-authenticated users
        }
    }

    public AuctionsStateDTO getAuctionsStats() {
        AuctionsStateDTO stats = new AuctionsStateDTO();
        stats.setTotalAuctions(auctionRepository.countByStatus(AuctionStatus.APPROVED));
        stats.setPendingRequests(auctionRepository.countByStatus(AuctionStatus.PENDING));
        stats.setActiveUsers(0L);  // Set to 0L for now or implement the real logic
        stats.setTotalRevenue(0L); // Set to 0L for now or implement the real logic
        return stats;
    }

    @Override
    public Page<AuctionDTO> findPendingAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionRepository.findByStatus(AuctionStatus.PENDING, pageable);
        return auctions.map(auction -> {
            AuctionDTO dto = auctionMapper.toDTO(auction);
            if (auction.getProduct() != null && auction.getOwner() != null) {
                dto.getProduct().setOwnerEmail(auction.getOwner().getEmail());
            }
            // Set duration with a default value of 0 if null
            dto.setDuration(auction.getAuctionDurationInHours() != null ? auction.getAuctionDurationInHours() : 0);
            return dto;
        });
    }

    @Override
    @Transactional
    public AuctionDTO approveAuction(UUID auctionId, AppUser adminUser) {
        // Verify admin role

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found with ID: " + auctionId));

        if (auction.getStatus() != AuctionStatus.PENDING) {
            throw new InvalidAuctionStatusException("Auction must be in PENDING status to be approved");
        }

        auction.setStatus(AuctionStatus.APPROVED);
        auction.setStartTime(LocalDateTime.now());

        // Set endTime for TIMED auctions
        if (auction.getAuctionType() == AuctionType.TIMED) {
            Integer duration = auction.getAuctionDurationInHours();
            if (duration == null || duration <= 0) {
                throw new InvalidAuctionException("Invalid auction duration for timed auction");
            }
            auction.setEndTime(auction.getStartTime().plusHours(duration));
        }

        auction = auctionRepository.save(auction);
        return auctionMapper.toDTO(auction);
    }

    @Override
    @Transactional
    public AuctionDTO rejectAuction(UUID auctionId, String reason, AppUser adminUser) {
        // Verify admin role

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found with ID: " + auctionId));

        if (auction.getStatus() != AuctionStatus.PENDING) {
            throw new InvalidAuctionStatusException("Auction must be in PENDING status to be rejected");
        }

        auction.setStatus(AuctionStatus.REJECTED);
        auction.setReason(reason);
        auction.setStartTime(null);
        auction.setEndTime(null);

        auction = auctionRepository.save(auction);
        return auctionMapper.toDTO(auction);
    }

    @Override
    public Optional<Auction> findById(UUID auctionId) {
        return auctionRepository.findById(auctionId);
    }


    public Page<AuctionDTO> findAuctionsByEmail(String email, Pageable pageable) {
        AppUser user = userService.findUserByEmail(email);
        Page<Auction> auctions = auctionRepository.findByOwnerEmail(email, pageable);
        return auctions.map(auction -> {
            AuctionDTO dto = auctionMapper.toDTO(auction);

            // Populate the bidders list from the bids
            List<ProfileDTO> bidders = auction.getBids().stream()
                    .map(bid -> {
                        ProfileDTO profileDTO = new ProfileDTO();
                        profileDTO.setEmail(bid.getUser().getEmail());
                        profileDTO.setProfileIdentifier(bid.getUser().getProfileIdentifier());
                        profileDTO.setPhoneNumber(bid.getUser().getPhoneNumber());
                        profileDTO.setFirstName(bid.getUser().getFirstName());
                        profileDTO.setLastName(bid.getUser().getLastName());
                        profileDTO.setImageUrl(bid.getUser().getImageUrl());
                        profileDTO.setCoverImageUrl(bid.getUser().getCoverImageUrl());
                        return profileDTO;
                    })
                    .collect(Collectors.toList());

            dto.setBidders(bidders);

            // Check if the current user liked the auction
            boolean likedByCurrentUser = auction.getAuctionReactions().stream()
                    .anyMatch(reaction -> reaction.getUser().getEmail().equals(email));
            dto.setLikedByCurrentUser(likedByCurrentUser);

            return dto;
        });
    }

    @Override
    public Page<AuctionDTO> findLikedAuctionsByEmail(String email, Pageable pageable) {
        Page<Auction> likedAuctions = auctionRepository.findByAuctionReactionsUserEmail(email, pageable);

        return likedAuctions.map(auction -> {
            AuctionDTO dto = auctionMapper.toDTO(auction);

            // Optionally, mark the auction as liked by the current user
            dto.setLikedByCurrentUser(true);

            // Populate additional fields if needed (e.g., bidders)
            List<ProfileDTO> bidders = auction.getBids().stream()
                    .map(bid -> {
                        ProfileDTO profileDTO = new ProfileDTO();
                        profileDTO.setEmail(bid.getUser().getEmail());
                        profileDTO.setProfileIdentifier(bid.getUser().getProfileIdentifier());
                        profileDTO.setPhoneNumber(bid.getUser().getPhoneNumber());
                        profileDTO.setFirstName(bid.getUser().getFirstName());
                        profileDTO.setLastName(bid.getUser().getLastName());
                        profileDTO.setImageUrl(bid.getUser().getImageUrl());
                        profileDTO.setCoverImageUrl(bid.getUser().getCoverImageUrl());
                        return profileDTO;
                    })
                    .collect(Collectors.toList());
            dto.setBidders(bidders);

            return dto;
        });
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
        auction.setStartingPrice(request.getStartingPrice());
        auction.setCurrentPrice(request.getStartingPrice());
        auction.setStatus(AuctionStatus.PENDING);
        auction.setStartTime(null);
        auction.setEndTime(null);

        // Calculate and set the endTime for non-instant auctions
        if (request.isInstantAuction()) {
            auction.setAuctionType(AuctionType.INSTANT);
        } else {
            auction.setAuctionType(AuctionType.TIMED);
            if (request.getAuctionDurationInHours() == null || request.getAuctionDurationInHours() <= 0) {
                throw new InvalidAuctionException("Auction duration must be greater than zero for non-instant auctions");
            }
            auction.setAuctionDurationInHours(request.getAuctionDurationInHours());
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

        // 3. Validate starting price (example: must be >= 0)
        if (request.getStartingPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAuctionException("Starting price cannot be negative");
        }
    }
}
