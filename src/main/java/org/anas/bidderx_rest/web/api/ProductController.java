package org.anas.bidderx_rest.web.api;


import org.anas.bidderx_rest.service.ProductService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.ProductDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable UUID id) {
        ProductDTO productDTO = productService.findProductById(id);
        return ResponseEntity.ok(new ApiResponse<>("Product retrieved successfully", productDTO));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getUserAvailableProducts(
            @RequestParam String email
    ) {
        List<ProductDTO> availableProducts = productService.findAvailableProductsByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("Products retrieved successfully", availableProducts));
    }
}
