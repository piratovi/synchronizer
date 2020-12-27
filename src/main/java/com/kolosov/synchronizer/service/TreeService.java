package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.TreeSync;
import com.kolosov.synchronizer.repository.TreeSyncRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TreeService {

    private final TreeSyncRepository treeSyncRepository;

    public Optional<TreeSync> findTreeSync() {
        List<TreeSync> treeSyncs = treeSyncRepository.findAll();
        if (treeSyncs.isEmpty()) {
            return Optional.empty();
        }
        if (treeSyncs.size() > 1) {
            throw new RuntimeException("More than 1 tree in DB");
        }
        return Optional.of(treeSyncs.get(0));
    }

    public TreeSync getTreeSync() {
        return findTreeSync().orElseThrow(() -> new RuntimeException("No tree in DB"));
    }

    //TODO maybe remove
    public void save(TreeSync newTreeSync) {
        treeSyncRepository.save(newTreeSync);
    }
}
