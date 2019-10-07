package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileEntityRepo extends JpaRepository<FileEntity, Long> {

}
