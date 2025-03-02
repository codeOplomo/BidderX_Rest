package org.anas.bidderx_rest.web.vm;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CreateAuctionVM {
    private UUID existingProductId; // null if creating a new product

    // Auction fields
    private String title;
    private String description;
    private BigDecimal startingPrice;
    @JsonProperty("isInstantAuction")
    private boolean isInstantAuction;
    private Integer auctionDurationInHours; // Used for non-instant auctions

    // New product fields - used only when existingProductId is null
    private String productTitle;
    private String productDescription;
    private String condition;
    private String manufacturer;
    private Date productionDate;
    private UUID collectionId;
    private String productImageUrl;
    private List<String> featuredImageUrls;

    private UUID categoryId;


    public CreateAuctionVM() {
    }

    public UUID getExistingProductId() {
        return existingProductId;
    }

    public void setExistingProductId(UUID existingProductId) {
        this.existingProductId = existingProductId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isInstantAuction() {
        return isInstantAuction;
    }

    public void setInstantAuction(boolean instantAuction) {
        isInstantAuction = instantAuction;
    }

    public Integer getAuctionDurationInHours() {
        return auctionDurationInHours;
    }

    public void setAuctionDurationInHours(Integer auctionDurationInHours) {
        this.auctionDurationInHours = auctionDurationInHours;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public UUID getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(UUID collectionId) {
        this.collectionId = collectionId;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getFeaturedImageUrls() {
        return featuredImageUrls;
    }

    public void setFeaturedImageUrls(List<String> featuredImageUrls) {
        this.featuredImageUrls = featuredImageUrls;
    }

}
