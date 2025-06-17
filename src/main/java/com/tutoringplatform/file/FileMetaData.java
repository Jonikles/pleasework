package com.tutoringplatform.file;

import java.time.LocalDateTime;

public class FileMetaData {
    private String fileId;
    private String userId;
    private String originalFileName;
    private String fileType;
    private String storedFileName;
    private LocalDateTime uploadTime;

    public FileMetaData(String fileId, String userId, String originalFileName,
            String fileType, String storedFileName) {
        this.fileId = fileId;
        this.userId = userId;
        this.originalFileName = originalFileName;
        this.fileType = fileType;
        this.storedFileName = storedFileName;
        this.uploadTime = LocalDateTime.now();
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    public String getUserId() {
        return userId;
    }    

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }
    
    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
}