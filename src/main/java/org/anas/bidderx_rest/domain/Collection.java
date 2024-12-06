package org.anas.bidderx_rest.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "collections")
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCollection> productCollections;

    // Many-to-One relationship with User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    // Constructors
    public Collection() {
    }

    // Method to add a product to the collection
    public void addProductCollection(ProductCollection productCollection) {
        if (productCollection != null) {
            if (this.productCollections == null) {
                this.productCollections = new ArrayList<>();
            }
            this.productCollections.add(productCollection);
            productCollection.setCollection(this);
        }
    }

    // Method to remove a product from the collection
    public void removeProductCollection(ProductCollection productCollection) {
        if (productCollection != null && this.productCollections != null) {
            this.productCollections.remove(productCollection);
            productCollection.setCollection(null);
        }
    }

    // Method to check if a product is already in the collection
    public boolean containsProduct(Product product) {
        if (this.productCollections == null || product == null) {
            return false;
        }
        return this.productCollections.stream()
                .anyMatch(pc -> pc.getProduct().equals(product));
    }

    // Method to get number of products in the collection
    public int getProductCount() {
        return this.productCollections == null ? 0 : this.productCollections.size();
    }

    // Method to validate collection attributes
    public boolean isValid() {
        return this.name != null && !this.name.trim().isEmpty()
                && this.appUser != null;
    }

    // Method to update collection details
    public void updateDetails(String name, String description) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        this.description = description != null ? description.trim() : null;
    }

    // Method to clear all products from the collection
    public void clearProducts() {
        if (this.productCollections != null) {
            this.productCollections.clear();
        }
    }

    // Optional: Method to get all products in the collection
    public List<Product> getProducts() {
        if (this.productCollections == null) {
            return Collections.emptyList();
        }
        return this.productCollections.stream()
                .map(ProductCollection::getProduct)
                .collect(Collectors.toList());
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<ProductCollection> getProductCollections() {
        return productCollections;
    }

    public void setProductCollections(List<ProductCollection> productCollections) {
        this.productCollections = productCollections;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
