package com.example.shop_project.product.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageService {
    public String uploadImage(String subPath, MultipartFile image) {
        String imageDirectoryPath = "C:/webserver_storage/"+ subPath +"/";
        File imageDirectory = new File(imageDirectoryPath);

        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs();
        }

        String extension = "";
        if (image != null) {
            extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
            if (extension != null) {
            } else {
                throw new IllegalArgumentException("No file extension");
            }
        }

        String savePath = imageDirectoryPath;
        String saveFilename = UUID.randomUUID() +"."+ extension;
        String fullPath = savePath + saveFilename;
        File file = new File(fullPath);

        try {
            image.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Image upload failed: " + e.getMessage());
        }
        String dbPath = "/upload/"+ subPath +"/" + saveFilename;

        return dbPath;
    }

    public void deleteImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            String localPath = "C:/webserver_storage/" + imagePath.replaceFirst("/upload/", "");
            File file = new File(localPath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    throw new RuntimeException("Failed to delete image: " + imagePath);
                }
            }
        }
    }
}
