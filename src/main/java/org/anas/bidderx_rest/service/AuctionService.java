package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
import org.anas.bidderx_rest.domain.enums.AuctionStatus;
import org.anas.bidderx_rest.domain.enums.AuctionType;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.anas.bidderx_rest.service.dto.AuctionsStateDTO;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AuctionService {
    Page<AuctionDTO> findWonAuctionsByEmail(String email, Pageable pageable);

    void endAuction(UUID auctionId);

    List<AuctionDTO> searchAuctions(String query, int limit);

    Page<AuctionDTO> getAuctionsByFilter(UUID categoryId, Double minPrice, Double maxPrice, AuctionStatus status, AuctionType type, String query, Pageable pageable);

    Page<AuctionDTO> getLiveAuctions(Pageable pageable, AppUser user);

    AuctionDTO getAuctionById(UUID auctionId, AppUser user);

    AuctionsStateDTO getAuctionsStats();

    AuctionDTO approveAuction(UUID auctionId, AppUser adminUser);

    AuctionDTO rejectAuction(UUID auctionId, String reason, AppUser adminUser);

    Page<AuctionDTO> findPendingAuctions(Pageable pageable);

    Optional<Auction> findById(UUID auctionId);

    Page<AuctionDTO> findAuctionsByEmail(String email, Pageable pageable);

    Page<AuctionDTO> findLikedAuctionsByEmail(String email, Pageable pageable);


    AuctionDTO createAuction(CreateAuctionVM request, MultipartFile mainImage, List<MultipartFile> additionalImages, @AuthenticationPrincipal AppUser authenticatedUser) ;

}
