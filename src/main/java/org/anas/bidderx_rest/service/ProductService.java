package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Product;
import org.anas.bidderx_rest.service.dto.ProductDTO;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.anas.bidderx_rest.web.vm.ProductVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product findById(UUID id);

    List<ProductDTO> getCollectionProducts(UUID collectionId);

    void handleProductImages(
            CreateAuctionVM request,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages
    );
    void handleProductImages(
            ProductVM request,
            MultipartFile mainImage,
            List<MultipartFile> additionalImages
    );
    ProductDTO createProduct(ProductVM request, MultipartFile mainImage, List<MultipartFile> additionalImages, String username);

    ProductDTO findProductById(UUID id);

    Page<ProductDTO> findAvailableProductsByEmail(String email, Pageable pageable);

    Product getOrCreateProduct(CreateAuctionVM request, AppUser owner);

    boolean isProductInAuction(UUID productId);
}
