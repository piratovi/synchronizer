package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.Sync;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SyncRepositoryTest {

    @Autowired
    SyncRepository repository;

    @Test
    void checkDelete() {
        repository.deleteById(1);
    }

    @Test
    @Transactional
    void checkNested() {
        List<Sync> syncs = repository.findAll();
        Sync sync = syncs.get(0);
        List<Sync> nestedSyncs = sync.getNestedSyncs();
        System.out.println();
    }
}