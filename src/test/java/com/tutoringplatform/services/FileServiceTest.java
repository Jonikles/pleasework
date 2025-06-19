package com.tutoringplatform.services;

import com.tutoringplatform.file.IFileRepository;
import com.tutoringplatform.file.FileMetaData;
import com.tutoringplatform.file.FileService;
import com.tutoringplatform.file.exception.FileNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private IFileRepository fileRepository;

    @Mock
    private MultipartFile multipartFile;

    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileService = new FileService(fileRepository);
    }

    @Test
    void storeFile_Success() throws Exception {
        // Arrange
        String userId = "user123";
        String fileType = "profile";
        String fileName = "profile.jpg";
        String contentType = "image/jpeg";
        byte[] content = "fake image content".getBytes();

        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(content));

        // Act
        String fileId = fileService.storeFile(userId, multipartFile, fileType);

        // Assert
        assertNotNull(fileId);
        verify(fileRepository).save(any(FileMetaData.class));
    }

    @Test
    void storeFile_InvalidFileName_ThrowsException() throws Exception {
        // Arrange
        String userId = "user123";
        String fileType = "profile";
        String invalidFileName = "../../../etc/passwd";

        when(multipartFile.getOriginalFilename()).thenReturn(invalidFileName);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> fileService.storeFile(userId, multipartFile, fileType));
    }

    @Test
    void storeFile_InvalidFileType_ThrowsException() throws Exception {
        // Arrange
        String userId = "user123";
        String fileType = "profile";
        String fileName = "document.pdf";
        String contentType = "application/pdf";

        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getContentType()).thenReturn(contentType);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> fileService.storeFile(userId, multipartFile, fileType));
    }

    @Test
    void deleteFile_Success() throws Exception {
        // Arrange
        String fileId = "file123";
        FileMetaData metadata = new FileMetaData(fileId, "user123", "profile.jpg",
                "profile", "file123.jpg");

        when(fileRepository.findById(fileId)).thenReturn(metadata);

        // Act
        fileService.deleteFile(fileId);

        // Assert
        verify(fileRepository).delete(fileId);
    }

    @Test
    void deleteFile_NotFound_ThrowsException() {
        // Arrange
        String fileId = "nonexistent";
        when(fileRepository.findById(fileId)).thenReturn(null);

        // Act & Assert
        assertThrows(FileNotFoundException.class,
                () -> fileService.deleteFile(fileId));
    }
}