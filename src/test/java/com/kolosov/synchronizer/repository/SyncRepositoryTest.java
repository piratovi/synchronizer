package com.kolosov.synchronizer.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SyncRepositoryTest {

    @Autowired
    SyncRepository repository;

    @Test
    void checkDelete() {
        repository.deleteById(867);
    }
}