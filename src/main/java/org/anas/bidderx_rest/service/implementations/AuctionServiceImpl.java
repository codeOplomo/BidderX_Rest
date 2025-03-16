package org.anas.bidderx_rest.service.implementations;

import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.*;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.anas.bidderx_rest.domain.enums.AuctionType;
import org.anas.bidderx_rest.domain.enums.TransactionType;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.repository.AuctionRepository;
import org.anas.bidderx_rest.schedulers.AuctionScheduler;
import org.anas.bidderx_rest.service.*;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.anas.bidderx_rest.service.dto.AuctionsStateDTO;
import org.anas.bidderx_rest.service.dto.OwnerRankingDTO;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppUserMapper;
import org.anas.bidderx_rest.service.dto.mapper.AuctionMapper;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuctionServiceImpl implements AuctionService {
    private final UserService userService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final AuctionRepository auctionRepository;
    private final AuctionMapper auctionMapper;
    private final AppUserMapper profileMapper;
    private final ProductService productService;
    private final AuctionScheduler auctionScheduler;

    public AuctionServiceImpl(
            UserService userService,
            WalletService walletService,
            TransactionService transactionService,
            AuctionRepository auctionRepository,
            AuctionMapper auctionMapper,
            AppUserMapper profileMapper,
            ProductService productService,
            @Lazy AuctionScheduler auctionScheduler
    ) {
        this.userService = userService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.auctionRepository = auctionRepository;
        this.auctionMapper = auctionMapper;
        this.profileMapper = profileMapper;
        this.productService = productService;
        this.auctionScheduler = auctionScheduler;
    }



    @Override
    @Transactional
    public void endAuction(UUID auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found with ID: " + auctionId));

        // Determine the winner by finding the highest bid
        Bid winningBid = auction.getBids().stream()
                .max(Comparator.comparing(Bid::getBidAmount))
                .orElse(null);

        if (winningBid != null) {
            auction.setWinnerId(winningBid.getUser().getId());
            BigDecimal ownerRevenue = winningBid.getBidAmount();

            Wallet ownerWallet = walletService.findWalletByUser(auction.getOwner());
            Transaction transaction = new Transaction();
            transaction.setAmount(ownerRevenue);
            transaction.setType(TransactionType.EARNING);
            transaction.setWallet(ownerWallet);
            transaction.setDescription("Auction payout for auction: " + auction.getTitle());

            ownerWallet.setBalance(ownerWallet.getBalance().add(ownerRevenue));
            transactionService.save(transaction);
            walletService.saveWallet(ownerWallet);
        }

        // Update the auction status to ENDED
        auction.setStatus(AuctionStatus.ENDED);
        refundLosingBids(auction, winningBid);

        // Save the auction updates
        auctionRepository.save(auction);
    }

    public Page<OwnerRankingDTO> getOwnerRanking(LocalDateTime startDate, Pageable pageable) {
        Page<Object[]> results = auctionRepository.findOwnerRevenuesSince(startDate, pageable);

        return results.map(result ->
                new OwnerRankingDTO(
                        profileMapper.toProfileDTO((AppUser) result[0]),
                        (BigDecimal) result[1]
                )
        );
    }

    @Override
    public Page<AuctionDTO> findWonAuctionsByEmail(String email, Pageable pageable) {
        AppUser user = userService.findUserByEmail(email);
        Page<Auction> auctions = auctionRepository.findByWinnerIdAndStatus(
                user.getId(),
                AuctionStatus.ENDED,
                pageable
        );

        return auctions.map(auction -> {
            AuctionDTO dto = auctionMapper.toDTO(auction);
            // Populate winner details
            if (auction.getWinnerId() != null) {
                AppUser winner = userService.findUserById(auction.getWinnerId());
                dto.setWinner(profileMapper.toProfileDTO(winner));
            }
            return dto;
        });
    }



    private void refundLosingBids(Auction auction, Bid winningBid) {
        // Map to hold total refundable amount per bidder
        Map<UUID, BigDecimal> refundAmounts = new HashMap<>();

        for (Bid bid : auction.getBids()) {
            UUID bidderId = bid.getUser().getId();
            // For the winning bidder, only refund the bids that are not the winning bid
            if (winningBid != null && bidderId.equals(winningBid.getUser().getId())) {
                // Skip refund for the winning bid, but refund other bids from the same user
                if (!bid.getId().equals(winningBid.getId())) {
                    refundAmounts.merge(bidderId, bid.getBidAmount(), BigDecimal::add);
                }
            } else {
                // For losing bidders, refund the full bid amount
                refundAmounts.merge(bidderId, bid.getBidAmount(), BigDecimal::add);
            }
        }

        // Process refunds for each bidder
        refundAmounts.forEach((bidderId, amount) -> {
            // Retrieve the user and their wallet
            AppUser bidder = userService.findUserById(bidderId);
            Wallet wallet = walletService.findWalletByUser(bidder);

            // Refund the amount (credit the wallet)
            wallet.setBalance(wallet.getBalance().add(amount));
            walletService.saveWallet(wallet);
        });
    }

    public List<AuctionDTO> searchAuctions(String query, int limit) {
        String searchTerm = "%" + query.toLowerCase() + "%";

        List<Auction> auctions = auctionRepository.findBySearchTerm(searchTerm, limit);
        return auctions.stream()
                .map(auctionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Page<AuctionDTO> getAuctionsByFilter(
            UUID categoryId,
            Double minPrice,
            Double maxPrice,
            AuctionStatus status,
            AuctionType type,
            String query,
            Pageable pageable
    ) {
        Specification<Auction> spec = buildSpecification(categoryId, minPrice, maxPrice, status, type, query);
        return auctionRepository.findAll(spec, pageable).map(auctionMapper::toDTO);
    }

    private Specification<Auction> buildSpecification(
            UUID categoryId,
            Double minPrice,
            Double maxPrice,
            AuctionStatus status,
            AuctionType type,
            String query
    ) {
        Specification<Auction> spec = Specification.where(statusSpec(status))
                .and(categorySpec(categoryId))
                .and(minPriceSpec(minPrice))
                .and(maxPriceSpec(maxPrice))
                .and(typeSpec(type));

        if (query != null && !query.isEmpty()) {
            Specification<Auction> querySpec = (root, queryObj, cb) -> {
                String pattern = "%" + query.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                );
            };
            spec = spec.and(querySpec);
        }
        return spec;
    }



    private Specification<Auction> typeSpec(AuctionType type) {
        return (root, query, cb) -> {
            if (type == null) return null;
            return cb.equal(root.get("auctionType"), type);
        };
    }

    private Specification<Auction> statusSpec(AuctionStatus status) {
        return (root, query, cb) -> {
            if (status == null) return null;
            return cb.equal(root.get("status"), status);
        };
    }

    private Specification<Auction> categorySpec(UUID categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;
            return cb.equal(
                    root.get("product").get("category").get("id"),
                    categoryId
            );
        };
    }

    private Specification<Auction> minPriceSpec(Double minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) return null;
            BigDecimal min = BigDecimal.valueOf(minPrice);
            return cb.greaterThanOrEqualTo(
                    root.get("currentPrice"),
                    min
            );
        };
    }

    private Specification<Auction> maxPriceSpec(Double maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) return null;
            BigDecimal max = BigDecimal.valueOf(maxPrice);
            return cb.lessThanOrEqualTo(
                    root.get("currentPrice"),
                    max
            );
        };
    }


    @Override
    public Page<AuctionDTO> getLiveAuctions(Pageable pageable, AppUser user) {
        Page<Auction> auctions = auctionRepository.findByStatus(AuctionStatus.APPROVED, pageable);
        return auctions.map(auction -> {
            AuctionDTO dto = auctionMapper.toDTO(auction);
            dto.setDuration(auction.getAuctionDurationInHours() != null ? auction.getAuctionDurationInHours() : 0);
            setLikeStatus(auction, dto, user);
            return dto;
        });
    }


    @Override
    public AuctionDTO getAuctionById(UUID auctionId, AppUser user) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException("Auction not found"));

        AuctionDTO dto = auctionMapper.toDTO(auction);
        setLikeStatus(auction, dto, user);
        if (auction.getWinnerId() != null) {
            AppUser winnerUser = userService.findUserById(auction.getWinnerId());
            dto.setWinner(profileMapper.toProfileDTO(winnerUser));
        }
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

        // Basic counts based on status
        stats.setTotalAuctions(auctionRepository.countByStatus(AuctionStatus.APPROVED));
        stats.setPendingRequests(auctionRepository.countByStatus(AuctionStatus.PENDING));
        stats.setActiveAuctions(auctionRepository.countActiveAuctions());
        stats.setCompletedAuctions(auctionRepository.countCompletedAuctions());

        // Average winning bid from completed auctions
        stats.setAverageWinningBid(auctionRepository.findAverageWinningBid());

        // Total bids and likes
        stats.setTotalBids(auctionRepository.countTotalBids());
        stats.setTotalLikes(auctionRepository.countTotalLikes());

        // Ongoing auctions based on time window
        stats.setOngoingAuctions(auctionRepository.countOngoingAuctions());

        // Revenue per owner: Convert Object[] results to a Map<String, BigDecimal>
        List<Object[]> revenueList = auctionRepository.findRevenuePerOwner();
        Map<String, BigDecimal> revenuePerOwner = new HashMap<>();
        for (Object[] row : revenueList) {
            String ownerEmail = (String) row[0];
            BigDecimal revenue = (BigDecimal) row[1];
            revenuePerOwner.put(ownerEmail, revenue);
        }
        stats.setRevenuePerOwner(revenuePerOwner);

        // Today revenue: Define start and end of today
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime endOfToday = today.atTime(LocalTime.MAX);
        BigDecimal todayRev = auctionRepository.findTodayRevenue(startOfToday, endOfToday);
        stats.setTodayRevenue(todayRev != null ? todayRev.longValue() : 0L);

        // Average bid amount
        BigDecimal avgBid = auctionRepository.findAverageBidAmount();
        stats.setAverageBidAmount(avgBid != null ? avgBid.longValue() : 0L);

        // For fields like activeUsers or totalRevenue you can implement similar queries or business logic
        stats.setActiveUsers(0L);  // Replace with actual logic if available
        stats.setTotalRevenue(0L); // Replace with actual logic if available

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

        auctionScheduler.scheduleAuctionEnd(auction);
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

            if (auction.getStatus() == AuctionStatus.ENDED && auction.getWinnerId() != null) {
                AppUser winner = userService.findUserById(auction.getWinnerId()); // Fetch the winner
                if (winner != null) {
                    ProfileDTO winnerProfile = new ProfileDTO();
                    winnerProfile.setEmail(winner.getEmail());
                    winnerProfile.setProfileIdentifier(winner.getProfileIdentifier());
                    winnerProfile.setPhoneNumber(winner.getPhoneNumber());
                    winnerProfile.setFirstName(winner.getFirstName());
                    winnerProfile.setLastName(winner.getLastName());
                    winnerProfile.setImageUrl(winner.getImageUrl());
                    winnerProfile.setCoverImageUrl(winner.getCoverImageUrl());

                    dto.setWinner(winnerProfile);
                }
            }
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




    @Transactional
    public AuctionDTO createAuction(CreateAuctionVM request, MultipartFile mainImage, List<MultipartFile> additionalImages, @AuthenticationPrincipal AppUser authenticatedUser) {
        if (request.getExistingProductId() == null) {
            productService.handleProductImages(request, mainImage, additionalImages);
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
