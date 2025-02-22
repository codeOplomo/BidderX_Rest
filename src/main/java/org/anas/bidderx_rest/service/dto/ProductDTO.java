package org.anas.bidderx_rest.service.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProductDTO {
    private UUID id;
    private String title;
    private String description;
    private String condition;
    private String manufacturer;
    private Date productionDate;
    private UUID categoryId;
    private String imageUrl;
    private List<String> featuredImages;
    private List<CollectionDTO> collections;

    public ProductDTO() {
    }

    public ProductDTO(UUID id, String title, String description, String condition, String manufacturer, Date productionDate, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.condition = condition;
        this.manufacturer = manufacturer;
        this.productionDate = productionDate;
        this.imageUrl = imageUrl;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getFeaturedImages() { return featuredImages; }
    public void setFeaturedImages(List<String> featuredImages) { this.featuredImages = featuredImages; }

    public List<CollectionDTO> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionDTO> collections) {
        this.collections = collections;
    }
}
