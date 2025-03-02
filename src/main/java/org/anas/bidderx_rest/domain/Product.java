package org.anas.bidderx_rest.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "condition", nullable = false)
    private String condition;

    @Column(name = "manufacturer", nullable = false)
    private String manufacturer;

    @Column(name = "production_date", nullable = false)
    private Date productionDate;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCollection> productCollections;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    // Constructors
    public Product() {
    }

    public void addToCollection(AppCollection appCollection) {
        if (appCollection != null) {
            // Create a new ProductCollection instance
            ProductCollection productCollection = new ProductCollection();
            productCollection.setProduct(this);
            productCollection.setCollection(appCollection);
            productCollection.setAddedAt(LocalDateTime.now());

            // Ensure productCollections list is initialized
            if (this.productCollections == null) {
                this.productCollections = new ArrayList<>();
            }

            // Add to product's collections
            this.productCollections.add(productCollection);
        }
    }

    // Method to remove product from a specific collection
    public void removeFromCollection(AppCollection appCollection) {
        if (appCollection != null && this.productCollections != null) {
            // Remove matching ProductCollection entries
            this.productCollections.removeIf(pc ->
                    pc.getCollection().equals(appCollection)
            );
        }
    }

    // Method to get collections this product belongs to
    public List<AppCollection> getCollections() {
        if (this.productCollections == null) {
            return Collections.emptyList();
        }
        return this.productCollections.stream()
                .map(ProductCollection::getCollection)
                .collect(Collectors.toList());
    }

    // Validation method for product
    public boolean isValid() {
        return this.title != null && !this.title.trim().isEmpty()
                && this.description != null && !this.description.trim().isEmpty()
                && this.condition != null && !this.condition.trim().isEmpty()
                && this.manufacturer != null && !this.manufacturer.trim().isEmpty()
                && this.productionDate != null
                && this.category != null;
    }

    // Method to update product details
    public void updateDetails(String title, String description,
                              String condition, String manufacturer,
                              Date productionDate, Category category) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        if (description != null && !description.trim().isEmpty()) {
            this.description = description.trim();
        }
        if (condition != null && !condition.trim().isEmpty()) {
            this.condition = condition.trim();
        }
        if (manufacturer != null && !manufacturer.trim().isEmpty()) {
            this.manufacturer = manufacturer.trim();
        }
        if (productionDate != null) {
            this.productionDate = productionDate;
        }
        if (category != null) {
            this.category = category;
        }
    }

    // Check if product is in a specific collection
    public boolean isInCollection(AppCollection appCollection) {
        if (this.productCollections == null || appCollection == null) {
            return false;
        }
        return this.productCollections.stream()
                .anyMatch(pc -> pc.getCollection().equals(appCollection));
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<ProductCollection> getProductCollections() {
        return productCollections;
    }

    public void setProductCollections(List<ProductCollection> productCollections) {
        this.productCollections = productCollections;
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

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }
}
