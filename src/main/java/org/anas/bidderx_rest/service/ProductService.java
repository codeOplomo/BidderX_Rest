package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Product;
import org.anas.bidderx_rest.service.dto.ProductDTO;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductDTO findProductById(UUID id);

    Page<ProductDTO> findAvailableProductsByEmail(String email, Pageable pageable);

    Product getOrCreateProduct(CreateAuctionVM request, AppUser owner);

    boolean isProductInAuction(UUID productId);
}
