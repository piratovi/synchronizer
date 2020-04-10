package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.exceptions.ExceptionSupplier;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class Transporter {

    private final DirectOperationsService directOperations;
    private final SyncRepository syncRepository;

    public void transfer(List<Integer> ids) {
        ids.forEach(id -> {
            Sync sync = syncRepository.findById(id).orElseThrow(ExceptionSupplier.syncNotFound(id));
            List<FolderSync> parents = SyncUtils.getParents(sync);
            CollectionUtils.filter(parents, Sync::isNotSynchronized);
            List<Sync> syncs = new ArrayList<>(parents);
            syncs.add(sync);
            syncs.forEach(this::transfer);
        });
    }

    private void transfer(Sync sync) {
        if (sync.existOnPC) {
            directOperations.copyFileFromPcToPhone(sync);
            sync.existOnPhone = true;
        } else {
            directOperations.copyFileFromPhoneToPc(sync);
            sync.existOnPC = true;
        }
        syncRepository.save(sync);
    }

}
