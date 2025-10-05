package com.example.report_issues.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    // allowed mime types
    private final Set<String> allowedMime = Set.of("image/png", "image/jpeg", "image/jpg");

    @PostConstruct
    public void init() throws IOException {
        Path dir = Paths.get(uploadDir);
        if (Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
    }

    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !allowedMime.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only PNG/JPEG images are allowed");
        }

        // generate unique filename
        String original = file.getOriginalFilename();
        String ext = "";

        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        } else if ("image/png".equals(contentType)) ext = ".png";
        else ext = ".jpg";

        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return filename; // caller can build URL: /uploads/{filename}
    }
}
