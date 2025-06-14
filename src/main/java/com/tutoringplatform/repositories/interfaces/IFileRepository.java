package com.tutoringplatform.repositories.interfaces;

import com.tutoringplatform.models.FileMetaData;
import java.util.List;

public interface IFileRepository {
    FileMetaData findById(String fileId);

    List<FileMetaData> findByUserId(String userId);

    void save(FileMetaData metadata);

    void delete(String fileId);
}