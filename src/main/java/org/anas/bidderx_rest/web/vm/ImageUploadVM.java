package org.anas.bidderx_rest.web.vm;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class ImageUploadVM {
    @NotNull(message = "Image file is required")
    private MultipartFile image;

    public ImageUploadVM() {
    }

    public MultipartFile getImage() {
        return this.image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    public ImageUploadVM image(MultipartFile image) {
        this.image = image;
        return this;
    }

    @AssertTrue(message = "Uploaded file must be an image")
    public boolean isValidImage() {
        return image != null && image.getContentType() != null && image.getContentType().startsWith("image/");
    }

    @AssertTrue(message = "Image size must not exceed 5MB")
    public boolean isValidSize() {
        return image != null && image.getSize() <= 5_000_000;
    }
}
