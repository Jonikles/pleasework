package com.tutoringplatform.repositories.interfaces;

import com.tutoringplatform.models.FileMetaData;
import java.util.List;

public interface IFileRepository extends IRepository<FileMetaData> {
    List<FileMetaData> findByUserId(String userId);
}