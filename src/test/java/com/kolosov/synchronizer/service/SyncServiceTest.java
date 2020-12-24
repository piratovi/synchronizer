package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.repository.HistorySyncRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class SyncServiceTest {

    @Autowired
    private SyncService syncService;
}