package com.tutoringplatform.file;

import com.tutoringplatform.file.exception.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.net.MalformedURLException;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;
    private final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload/{userId}")
    public ResponseEntity<?> uploadFile(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String fileType) throws IOException {
        logger.debug("Uploading file for user: {}", userId);
        String fileId = fileService.storeFile(userId, file, fileType);
        return ResponseEntity.ok(Map.of("fileId", fileId, "fileName", file.getOriginalFilename()));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileId) throws FileNotFoundException, MalformedURLException {
        logger.debug("Downloading file: {}", fileId);
        Resource resource = fileService.loadFile(fileId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable String fileId) throws IOException, FileNotFoundException {
        logger.debug("Deleting file: {}", fileId);
        fileService.deleteFile(fileId);
        return ResponseEntity.ok("File deleted successfully");
    }
}