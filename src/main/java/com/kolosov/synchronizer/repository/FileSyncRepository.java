package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.FileSync;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileSyncRepository extends JpaRepository<FileSync, Long> {
}
