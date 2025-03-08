package org.anas.bidderx_rest.service.dto;

import java.util.UUID;

public class CategoryDTO {
    private UUID id;
    private String name;
    private Long productsCount;
    private Long auctionsCount;

    // getters and setters
    public CategoryDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProductsCount() {
        return productsCount;
    }

    public void setProductsCount(Long productsCount) {
        this.productsCount = productsCount;
    }

    public Long getAuctionsCount() {
        return auctionsCount;
    }

    public void setAuctionsCount(Long auctionsCount) {
        this.auctionsCount = auctionsCount;
    }
}
