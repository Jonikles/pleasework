package com.tutoringplatform.file;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileRepository implements IFileRepository {
    private Map<String, FileMetaData> files = new HashMap<>();

    @Override
    public FileMetaData findById(String fileId) {
        return files.get(fileId);
    }

    @Override
    public List<FileMetaData> findAll() {
        return new ArrayList<>(files.values());
    }

    @Override
    public void save(FileMetaData metadata) {
        files.put(metadata.getFileId(), metadata);
    }

    @Override
    public void update(FileMetaData metadata) {
        files.put(metadata.getFileId(), metadata);
    }

    @Override
    public void delete(String fileId) {
        files.remove(fileId);
    }
}