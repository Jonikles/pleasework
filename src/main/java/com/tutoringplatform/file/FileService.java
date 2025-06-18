package com.tutoringplatform.file;

import com.tutoringplatform.file.exception.FileNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.util.*;
import java.net.MalformedURLException;
import java.io.IOException;

@Service
public class FileService {

    private final Logger logger = LoggerFactory.getLogger(FileService.class);
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

    public String storeFile(String userId, MultipartFile file, String fileType) throws IOException {
        logger.debug("Storing file for user: {}", userId);
        // Validate file
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.contains("..")) {
            logger.warn("Tried to store file with invalid file name: {}", fileName);
            throw new IllegalArgumentException("Invalid file name");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (!isAllowedFileType(fileType, contentType)) {
            logger.warn("Tried to store file with invalid file type: {}", fileType);
            throw new IllegalArgumentException("File type not allowed");
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

        logger.info("File stored successfully for user: {}", userId);
        return fileId;
    }

    public Resource loadFile(String fileId) throws FileNotFoundException, MalformedURLException {
        FileMetaData metadata = fileRepository.findById(fileId);
        if (metadata == null) {
            logger.error("File not found: {}", fileId);
            throw new FileNotFoundException("File not found");
        }

        Path filePath = this.fileStorageLocation.resolve(metadata.getStoredFileName()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new MalformedURLException("File not found");
        }

        return resource;
    }

    public void deleteFile(String fileId) throws IOException, FileNotFoundException {
        FileMetaData metadata = fileRepository.findById(fileId);
        if (metadata == null) {
            logger.error("File not found: {}", fileId);
            throw new FileNotFoundException(fileId);
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