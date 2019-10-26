package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileEntityRepo extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByLocation(FileEntity.Location location);
}
