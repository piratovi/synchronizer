package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreeService {

    private final TreeSyncRepository treeSyncRepository;

    public TreeSync getTreeSync() {
        List<TreeSync> treeSyncs = treeSyncRepository.findAll();
        if (treeSyncs.isEmpty()) {
            return new TreeSync();
        }
        if (treeSyncs.size() > 1) {
            throw new RuntimeException("More than 1 tree in DB");
        }
        return treeSyncs.get(0);
    }

    public void save(TreeSync newTreeSync) {
        treeSyncRepository.save(newTreeSync);
    }
}
