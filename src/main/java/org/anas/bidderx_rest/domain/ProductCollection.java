package org.anas.bidderx_rest.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "product_collection")
public class ProductCollection
{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    private AppCollection appCollection;

    // Additional fields if needed, like added timestamp, etc.
    @Column(name = "added_at")
    private LocalDateTime addedAt;

    // Constructors
    public ProductCollection() {}

    public boolean isValid() {
        return this.product != null
                && this.appCollection != null
                && this.addedAt != null;
    }

    // Method to check how long the product has been in the collection
    public long getDaysInCollection() {
        if (this.addedAt == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(this.addedAt, LocalDateTime.now());
    }

    // Method to update added timestamp (useful for certain operations)
    public void updateAddedTimestamp() {
        this.addedAt = LocalDateTime.now();
    }

    public ProductCollection(Product product, AppCollection appCollection) {
        this.product = product;
        this.appCollection = appCollection;
        this.addedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public AppCollection getCollection() {
        return appCollection;
    }

    public void setCollection(AppCollection appCollection) {
        this.appCollection = appCollection;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }
}
