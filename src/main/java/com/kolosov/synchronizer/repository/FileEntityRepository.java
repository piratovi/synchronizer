package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.FileEntity;
import com.kolosov.synchronizer.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByLocation(Location location);

    @Transactional
    void deleteAllByLocation(Location location);
}
