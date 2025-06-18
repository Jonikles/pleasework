package com.tutoringplatform.file;

import com.tutoringplatform.shared.IRepository;

import java.util.List;

public interface IFileRepository extends IRepository<FileMetaData> {
    List<FileMetaData> findByUserId(String userId);
}