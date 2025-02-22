package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Product;
import org.anas.bidderx_rest.service.dto.ProductDTO;
import org.anas.bidderx_rest.web.vm.CreateAuctionVM;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductDTO findProductById(UUID id);

    List<ProductDTO> findAvailableProductsByEmail(String email);

    Product getOrCreateProduct(CreateAuctionVM request, AppUser owner);

    boolean isProductInAuction(UUID productId);
}
