package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.FeaturedImage;
import org.anas.bidderx_rest.repository.FeaturedImageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FeaturedImageService {
    private final FeaturedImageRepository featuredImageRepository;

    public FeaturedImageService(FeaturedImageRepository featuredImageRepository) {
        this.featuredImageRepository = featuredImageRepository;
    }

    public void saveFeaturedImage(UUID entityId, String entityType, String imageUrl) {
        FeaturedImage featuredImage = new FeaturedImage();
        featuredImage.setImageUrl(imageUrl);
        featuredImage.setEntityType(entityType);
        featuredImage.setEntityId(entityId);
        featuredImageRepository.save(featuredImage);
    }

    public void saveBatchFeaturedImages(UUID entityId, String entityType, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        List<FeaturedImage> featuredImages = imageUrls.stream()
                .map(url -> {
                    FeaturedImage image = new FeaturedImage();
                    image.setImageUrl(url);
                    image.setEntityType(entityType);
                    image.setEntityId(entityId);
                    return image;
                })
                .collect(Collectors.toList());

        featuredImageRepository.saveAll(featuredImages);
    }

    public List<String> getProductFeaturedImages(UUID productId) {
        return featuredImageRepository.findByEntityTypeAndEntityId("product", productId)
                .stream()
                .map(FeaturedImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
