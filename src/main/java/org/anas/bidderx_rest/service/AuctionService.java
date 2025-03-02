package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Auction;
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
    AuctionDTO getAuctionById(UUID auctionId, AppUser user);

    AuctionsStateDTO getAuctionsStats();

    AuctionDTO approveAuction(UUID auctionId, AppUser adminUser);

    AuctionDTO rejectAuction(UUID auctionId, String reason, AppUser adminUser);

    Page<AuctionDTO> findPendingAuctions(Pageable pageable);

    Optional<Auction> findById(UUID auctionId);

    Page<AuctionDTO> findAuctionsByEmail(String email, Pageable pageable);

    Page<AuctionDTO> findLikedAuctionsByEmail(String email, Pageable pageable);
    void handleProductImages(
            CreateAuctionVM request,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages
    );
    AuctionDTO createAuction(CreateAuctionVM request, MultipartFile mainImage, List<MultipartFile> additionalImages, @AuthenticationPrincipal AppUser authenticatedUser) ;

}
