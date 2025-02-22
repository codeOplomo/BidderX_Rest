package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.AuctionDTO;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AuctionService {
    void handleProductImages(
            CreateAuctionVM request,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages
    );
    AuctionDTO createAuction(CreateAuctionVM request, MultipartFile mainImage, List<MultipartFile> additionalImages, @AuthenticationPrincipal AppUser authenticatedUser) ;

}
