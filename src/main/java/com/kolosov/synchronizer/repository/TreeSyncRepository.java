package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.TreeSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreeSyncRepository extends JpaRepository<TreeSync, Integer> {

    default TreeSync findTree() {
        List<TreeSync> treeSyncs = findAll();
        if (treeSyncs.isEmpty()) {
            return null;
        }
        if (treeSyncs.size() > 1) {
            throw new RuntimeException("More than 1 tree in DB");
        }
        return treeSyncs.get(0);
    }

}
