package org.anas.bidderx_rest.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String storeImage(MultipartFile image, String folderName);

}
