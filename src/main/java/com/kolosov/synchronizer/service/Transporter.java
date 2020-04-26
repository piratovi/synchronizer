package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.enums.Location;
import com.kolosov.synchronizer.exceptions.ExceptionSupplier;
import com.kolosov.synchronizer.repository.SyncRepository;
import com.kolosov.synchronizer.utils.SyncUtils;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class Transporter {

    private final DirectOperationsService directOperations;
    private final SyncRepository syncRepository;

    @Synchronized
    public void transfer(List<Integer> ids) {
        log.info("Transferring start");
        List<Sync> syncsToTransfer = ids.stream()
                .map(id -> syncRepository.findById(id).orElseThrow(ExceptionSupplier.syncNotFound(id)))
                .map(SyncUtils::getFlatSyncs)
                .flatMap(Collection::stream)
                .filter(Sync::isNotSynchronized)
                .collect(Collectors.toList());
        syncsToTransfer.forEach(sync -> {
            List<FolderSync> parents = SyncUtils.getParents(sync);
            CollectionUtils.filter(parents, Sync::isNotSynchronized);
            List<Sync> syncs = new ArrayList<>(parents);
            syncs.add(sync);
            syncs.forEach(this::transfer);
        });
        log.info("Transferring end");
    }

    private void transfer(Sync sync) {
        Location locationTo;
        if (sync.existOnPC) {
            directOperations.transferFromPcToPhone(sync);
            sync.existOnPhone = true;
            locationTo = Location.PHONE;
        } else {
            directOperations.transferFromPhoneToPc(sync);
            sync.existOnPC = true;
            locationTo = Location.PC;
        }
        syncRepository.save(sync);
        log.info(sync.relativePath + " transferred to " + locationTo);
    }

}
