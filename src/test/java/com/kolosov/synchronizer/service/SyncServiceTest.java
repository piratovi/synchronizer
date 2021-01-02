package com.kolosov.synchronizer.service;

import com.kolosov.synchronizer.repository.TreeSyncRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

//TODO write later with test data initialization
//integration test
@SpringBootTest
@ExtendWith(SpringExtension.class)
class SyncServiceTest {

    @Autowired
    private SyncService syncService;
    @Autowired
    private TreeSyncRepository repository;


    @Test
    void test() {
        // setup

        // act

        // verify
    }
}