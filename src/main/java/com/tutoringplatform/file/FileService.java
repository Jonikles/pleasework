package com.tutoringplatform.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.*;
import java.util.*;

@Service
public class FileService {

    private final IFileRepository fileRepository;
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Autowired
    public FileService(IFileRepository fileRepository) {
        this.fileRepository = fileRepository;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create upload directory");
        }
    }

    public String storeFile(String userId, MultipartFile file, String fileType) throws Exception {
        // Validate file
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.contains("..")) {
            throw new Exception("Invalid file name");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!isAllowedFileType(fileType, contentType)) {
            throw new Exception("File type not allowed");
        }

        // Generate unique file ID
        String fileId = UUID.randomUUID().toString();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String storedFileName = fileId + extension;

        // Store file
        Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Store metadata
        FileMetaData metadata = new FileMetaData(fileId, userId, fileName, fileType, storedFileName);
        fileRepository.save(metadata);

        return fileId;
    }

    public Resource loadFile(String fileId) throws Exception {
        FileMetaData metadata = fileRepository.findById(fileId);
        if (metadata == null) {
            throw new Exception("File not found");
        }

        Path filePath = this.fileStorageLocation.resolve(metadata.getStoredFileName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new Exception("File not found");
        }

        return resource;
    }

    public void deleteFile(String fileId) throws Exception {
        FileMetaData metadata = fileRepository.findById(fileId);
        if (metadata == null) {
            throw new Exception("File not found");
        }

        Path filePath = this.fileStorageLocation.resolve(metadata.getStoredFileName()).normalize();
        Files.deleteIfExists(filePath);

        fileRepository.delete(fileId);
    }

    private boolean isAllowedFileType(String fileType, String contentType) {
        switch (fileType) {
            case "profile":
                return contentType != null && (contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg"));
            default:
                return false;
        }
    }
}