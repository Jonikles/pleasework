package com.tutoringplatform.file.exception;

public class FileNotFoundException extends FileException {
    private final String fileId;

    public FileNotFoundException(String fileId) {
        super("FILE_NOT_FOUND", "File not found: " + fileId);
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }
}
