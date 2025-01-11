package org.anas.bidderx_rest.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${app.upload.dir:uploads}")
    private String baseUploadDir;

    public String storeFile(MultipartFile file, String subDirectory) {
        try {
            String uploadDir = baseUploadDir + "/" + subDirectory;

            // Generate a unique filename
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadDir).toAbsolutePath().resolve(fileName);

            // Ensure directories exist
            Files.createDirectories(targetLocation.getParent());

            // Save the file
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/api/uploads/" + subDirectory + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }
}

