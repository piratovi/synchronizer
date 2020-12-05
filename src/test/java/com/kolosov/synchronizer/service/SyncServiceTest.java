package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.domain.HistorySync;
import com.kolosov.synchronizer.enums.ProposedAction;
import com.kolosov.synchronizer.repository.HistorySyncRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class SyncServiceTest {

    @Autowired
    HistorySyncRepository repository;

    @Autowired
    LocationService locationService;

    @Test
    @Transactional
    void dirtiesContextTest() {
//        HistorySync historySync = repository.findById(221270).get();
//        System.out.println(historySync);
//        historySync.action = ProposedAction.TRANSFER;
//        repository.save(historySync);
//        System.out.println(repository.findById(221270).get());
    }

    @Test
    void checkLocationConfig() {
        System.out.println(locationService);
    }
}