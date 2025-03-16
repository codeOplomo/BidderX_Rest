package org.anas.bidderx_rest.web.api;


import jakarta.validation.Valid;
import org.anas.bidderx_rest.service.ProductService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.ProductDTO;
import org.anas.bidderx_rest.web.vm.ProductVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("!principal.banned and hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Valid @RequestPart("product") ProductVM request,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "additionalImages", required = false) List<MultipartFile> additionalImages,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ProductDTO createdProduct = productService.createProduct(request, mainImage, additionalImages, userDetails.getUsername());
        return ResponseEntity.ok(new ApiResponse<>("Product created successfully", createdProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable UUID id) {
        ProductDTO productDTO = productService.findProductById(id);
        return ResponseEntity.ok(new ApiResponse<>("Product retrieved successfully", productDTO));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Page<ProductDTO>>> getUserAvailableProducts(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> availableProducts = productService.findAvailableProductsByEmail(email, pageable);
        return ResponseEntity.ok(new ApiResponse<>("Products retrieved successfully", availableProducts));
    }
}
