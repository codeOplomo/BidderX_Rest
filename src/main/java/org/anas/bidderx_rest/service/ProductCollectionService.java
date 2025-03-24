package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.ProductCollection;
import org.anas.bidderx_rest.repository.ProductCollectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductCollectionService {
    private final ProductCollectionRepository productCollectionRepository;

    public ProductCollectionService(ProductCollectionRepository productCollectionRepository) {
        this.productCollectionRepository = productCollectionRepository;
    }


    public List<ProductCollection> findByCollectionId(UUID collectionId) {
        return productCollectionRepository.findByAppCollectionId(collectionId);
    }

    public void save(ProductCollection productCollection) {
        productCollectionRepository.save(productCollection);
    }

    public Optional<ProductCollection> findByCollectionIdAndProductId(UUID collectionId, UUID productId) {
        return productCollectionRepository.findByAppCollectionIdAndProductId(collectionId, productId);
    }

    public void delete(ProductCollection productCollection) {
        productCollectionRepository.delete(productCollection);
    }
}
